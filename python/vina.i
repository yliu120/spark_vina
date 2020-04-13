// vina.i - SWIG interface for python vina module
%module vina
%{
#include "protos/vina.pb.h"
#include "cc/vina.h"
%}

%include "std_vector.i"
%include "std_string.i"

// Instantiate templates used by example

namespace std {
  %template(StringVector) vector<string>;
  %template(VinaResultVector) vector<VinaResult>;
}

// Parse the original header file
%include "protos/vina.pb.h"
%include "cc/vina.h"
