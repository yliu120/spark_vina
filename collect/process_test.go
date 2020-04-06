package process

import (
	"path/filepath"
	"spark_vina/collect/process"
	"testing"
)

func TestVisit(t *testing.T) {
	var files []string

	root := "../data/ligands"
	err := filepath.Walk(root, process.Visit(&files))
	if err != nil {
		panic(err)
	}
	if len(files) != 1 {
		t.Error("Expected 1 file.")
	}
}

func TestFetchAffinityFromFile(t *testing.T) {
	filepath := "../data/ligands/HB/AAMM/HBAAMM.xaa.pdbqt"
	process.FetchAffinityFromFile(&filepath)
}
