package org.spark.tools;

import com.google.common.util.concurrent.RateLimiter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spark_vina.SparkVinaUtils;

public final class ZincFilesToParquetV2Main {
  private static final Logger LOGGER = LoggerFactory.getLogger(ZincFilesToParquetV2Main.class);
  private static final int NUM_THREADS = 20;
  private static final int QPS = 2;
  private static final String URI_BASE = "http://files.docking.org/protomers/";

  public static void main(String[] args) {
    final Option sourceDirOption =
        Option.builder()
            .longOpt("source_dir")
            .required()
            .hasArg()
            .desc("The directory of the ligand in txt format.")
            .build();
    final Option outputFileOption =
        Option.builder()
            .longOpt("output_file")
            .required()
            .hasArg()
            .desc("The output file for writing mol2 file.")
            .build();

    Options options = new Options();
    options.addOption(sourceDirOption).addOption(outputFileOption);

    // Parse the command lin arguments.
    CommandLineParser parser = new DefaultParser();
    CommandLine cmdLine;
    try {
      cmdLine = parser.parse(options, args);
    } catch (ParseException parseException) {
      LOGGER.error(parseException.getMessage());
      new HelpFormatter().printHelp("ZincFilesToParquetV2", options);
      return;
    }

    final String sourceDir = cmdLine.getOptionValue(sourceDirOption.getLongOpt());
    final String outputFile = cmdLine.getOptionValue(outputFileOption.getLongOpt());
    Optional<List<String>> metaDataFilePaths = SparkVinaUtils
        .getAllLigandFilesInDirectory(sourceDir, Pattern
            .compile(".*.txt"));
    if (!metaDataFilePaths.isPresent() || metaDataFilePaths.get().isEmpty()) {
      LOGGER.error("Collecting txt files failed.");
      return;
    }

    List<HttpRequest> requests = metaDataFilePaths.get().stream()
        .flatMap(
            path -> {
              List<String> lines = new ArrayList<>();
              try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path))) {
                for (String line = bufferedReader.readLine(); line != null;
                    line = bufferedReader.readLine()) {
                  if (line.isEmpty()) {
                    continue;
                  }
                  lines.add(line);
                }
              } catch (IOException e) {
                e.printStackTrace();
              }
              return lines.stream();
            }
        )
        .map(
            line -> CompoundMetadata.parseFromTSVLine(line).getProtomerId()
        )
        .distinct()
        .map(protomerId -> {
          StringBuilder builder =  new StringBuilder(URI_BASE);
          String lastSixDigits = protomerId.substring(protomerId.length() - 6);
          builder.append(lastSixDigits.substring(0, 2));
          builder.append('/');
          builder.append(lastSixDigits.substring(2, 4));
          builder.append('/');
          builder.append(lastSixDigits.substring(4));
          builder.append('/');
          builder.append(protomerId);
          builder.append(".mol2.gz");
          return builder.toString();
        })
        .map(URI::create)
        .map(uri -> HttpRequest.newBuilder(uri).build())
        .collect(Collectors.toList());

    LOGGER.info("Created {} requests in total.", requests.size());
    ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
    HttpClient client = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(200))
        .executor(executor)
        .followRedirects(Redirect.NORMAL)
        .priority(1)
        .build();

    RateLimiter rateLimiter = RateLimiter.create(QPS);
    List<String> mol2Strings = new ArrayList<>();
    CompletableFuture.allOf(requests.stream()
        .map(request -> {
          rateLimiter.acquire();
          LOGGER.info("Fetching request: {}", request);
          return client.sendAsync(request, BodyHandlers.ofInputStream())
              .thenApply(response -> {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                try (InputStream is = new GZIPInputStream(
                    response.body()); ByteArrayOutputStream autoCloseOs = os) {
                  is.transferTo(autoCloseOs);
                } catch (IOException e) {
                  LOGGER
                      .error("Got IOException when reading the response body: {}", e.getMessage());
                }
                return new String(os.toByteArray(), StandardCharsets.UTF_8);
              })
              .thenAccept(mol2String -> {
                if (!mol2String.contains("ZINC")) {
                  LOGGER.error("Got exception response: {}", mol2String);
                }
                mol2Strings.add(mol2String);
              });
        })
        .toArray(CompletableFuture<?>[]::new)).join();
    executor.shutdown();

    LOGGER.info("Writing {} mol2 strings in total.", mol2Strings.size());
    try (BufferedWriter bufferedWriter = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8"))) {
      for (String mol2String : mol2Strings) {
        bufferedWriter.write(mol2String);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
