package main

import (
	"flag"
	"path/filepath"
	"spark_vina/collect/process"
)

func main() {
	rootPtr := flag.String("root", "output", "output root directory")
	outputPtr := flag.String("output", "candidates.zip", "candidate zip file")
	howManyPtr := flag.Int64(
		"howmany", 1000, "the number of candidates to output")

	flag.Parse()

	var files []string
	err := filepath.Walk(*rootPtr, process.Visit(&files))
	if err != nil {
		panic(err)
	}

	process.CreateZip(process.TakeOrderedN(&files, *howManyPtr), outputPtr)
}
