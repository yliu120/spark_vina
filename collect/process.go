package process

import (
	"archive/zip"
	"bufio"
	"fmt"
	"io"
	"log"
	"os"
	"path"
	"path/filepath"
	"sort"
	"strconv"
	"strings"
	"sync"
)

type Candidate struct {
	Path     string
	Affinity float64
}

type ByAffinity []Candidate

func (a ByAffinity) Len() int           { return len(a) }
func (a ByAffinity) Swap(i, j int)      { a[i], a[j] = a[j], a[i] }
func (a ByAffinity) Less(i, j int) bool { return a[i].Affinity < a[j].Affinity }

func Visit(files *[]string) filepath.WalkFunc {
	return func(filepath string, info os.FileInfo, err error) error {
		if err != nil {
			log.Fatal(err)
		}
		if path.Ext(filepath) == ".pdbqt" {
			*files = append(*files, filepath)
		}
		return nil
	}
}

// 1.0 is a dummy value. == nil
func FetchAffinityFromFile(filepath *string) float64 {
	file, open_err := os.Open(*filepath)
	if open_err != nil {
		log.Print(open_err)
		return 1.0
	}
	defer file.Close()

	var affinity_line string
	scanner := bufio.NewScanner(file)
	for scanner.Scan() {
		affinity_line = scanner.Text()
		if strings.Contains(affinity_line, "AFFINITY") {
			break
		}
	}

	affinity_line = strings.Trim(affinity_line, "\n")
	splits := strings.Split(affinity_line, " ")
	fmt.Print()
	for i := len(splits) - 1; i >= 0; i-- {
		if splits[i] != "" {
			s, err := strconv.ParseFloat(splits[i], 64)
			if err == nil {
				return s
			}
			log.Print(err)
			return 1.0
		}
	}
	log.Output(1, fmt.Sprintf("No affinity found in %s.", *filepath))
	return 1.0
}

func TakeOrderedN(files *[]string, n int64) []string {
	candidates := make([]Candidate, len(*files))
	var wg sync.WaitGroup
	for i := range *files {
		f := (*files)[i]
		wg.Add(1)
		go func(candidate *Candidate) {
			affinity := FetchAffinityFromFile(&f)
			*candidate = Candidate{f, affinity}
			wg.Done()
		}(&candidates[i])
	}
	wg.Wait()
	sort.Sort(ByAffinity(candidates))

	var candidate_paths []string
	for _, c := range candidates {
		candidate_paths = append(candidate_paths, c.Path)
	}
	return candidate_paths[:n]
}

func CreateZip(candidates []string, output_path *string) {
	zfile, err := os.Create(*output_path)
	if err != nil {
		log.Fatal(err)
	}
	defer zfile.Close()

	zip_writer := zip.NewWriter(zfile)
	defer zip_writer.Close()

	for _, f := range candidates {
		file_to_zip, err := os.Open(f)
		if err != nil {
			log.Print(err)
			continue
		}
		defer file_to_zip.Close()

		writer, err := zip_writer.Create(f)
		if err != nil {
			log.Print(err)
			continue
		}
		_, err = io.Copy(writer, file_to_zip)
		if err != nil {
			log.Print(err)
			continue
		}
	}
}
