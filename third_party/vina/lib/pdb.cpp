/*

   Copyright (c) 2006-2010, The Scripps Research Institute

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

   Author: Dr. Oleg Trott <ot14@columbia.edu>, 
           The Olson Lab, 
           The Scripps Research Institute

*/

#include <string>

#include "absl/strings/numbers.h"
#include "absl/strings/string_view.h"
#include "pdb.h"
#include "parse_error.h"
#include "file.h"

struct bad_conversion {};

void pdb::check(fl min_distance) const {
	VINA_FOR_IN(i, atoms) {
		const pdb_atom& a = atoms[i];
		VINA_RANGE(j, i+1, atoms.size()) {
			const pdb_atom& b = atoms[j];
			fl d2 = vec_distance_sqr(a.coords, b.coords);
			if(d2 < sqr(min_distance)) {
				std::cout << "The distance between " 
					<< a.id << ":" << a.name << ":" << a.element
					<< " and " 
					<< b.id << ":" << b.name << ":" << b.element
					<< " is " << std::sqrt(d2) << '\n';
			}
		}
	}
}

pdb_atom string_to_pdb_atom(absl::string_view atom_string) {
  if (atom_string.size() < 66) throw bad_conversion();  // b-factor is in 61-66
  pdb_atom result;
  if (absl::SimpleAtoi(atom_string.substr(6, 5), &result.id) &&
      absl::SimpleAtoi(atom_string.substr(22, 4), &result.residue_id) &&
      absl::SimpleAtod(atom_string.substr(30, 8), &result.coords[0]) &&
      absl::SimpleAtod(atom_string.substr(38, 8), &result.coords[1]) &&
      absl::SimpleAtod(atom_string.substr(46, 8), &result.coords[2]) &&
      absl::SimpleAtod(atom_string.substr(60, 6), &result.b_factor)) {
    result.name = std::string(atom_string.substr(12, 4));
    result.residue_name = std::string(atom_string.substr(17, 3));
    result.element = std::string(atom_string.substr(76, 2));
    return result;
  }
  throw bad_conversion();
}

pdb parse_pdb(const path& name) {
	ifile in(name);

	pdb tmp;

	std::string str;
	unsigned count = 0;
	while(std::getline(in, str)) {
		++count;
		if(starts_with(str, "ATOM  ") || starts_with(str, "HETATM")) {
			try {
				tmp.atoms.push_back(string_to_pdb_atom(str));
			}
			catch(...) { // bad_conversion, presumably; but can lexical_cast throw its own errors?
				throw parse_error(name, count, "ATOM syntax incorrect");
			}
		}
	}
	return tmp;
}
