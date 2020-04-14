// vina.i - SWIG interface for python vina module
%module vina
%{
#include "cc/vina.h"
%}

%include "std_vector.i"
%include "std_string.i"

// Instantiate templates used by example

namespace std {
  %template(StringVector) vector<string>;
}

%ignore VinaDock::vina_fit(const std::vector<std::string>&, double);
%extend VinaDock {
  std::vector<std::string> _vina_fit_swig(
      const std::vector<std::string>& ligand_strs, double filter_limit) {
     std::vector<VinaResult> proto_results = $self->vina_fit(
         ligand_strs, filter_limit);
     std::vector<std::string> results;
     for (const auto& proto_result : proto_results) {
       results.push_back(proto_result.SerializeAsString());
     }
     return results;
  }

  %pythoncode %{
    def vina_fit(self, ligand_strs, filter_limit):
        if not ligand_strs:
            return []
        if filter_limit < 0:
            raise ValueError(
                "VinaDock::vina_fit: filter_limit cannot be less than 0.")
        vina_result_bytes = self._vina_fit_swig(ligand_strs, filter_limit)
        result = []
        for result_bytes in vina_result_bytes:
            vina_result = vina_pb2.VinaResult()
            vina_result.ParseFromString(
                result_bytes.encode('utf-8', errors='surrogateescape'))
            result.append(vina_result)
        return result
    %}
}

// Parse the original header file
%include "cc/vina.h"

%pythonbegin %{
from protos import vina_pb2
%}
