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

#ifndef VINA_QUATERNION_H
#define VINA_QUATERNION_H

// #include <boost/math/quaternion.hpp>

#include "common.h"
#include "random.h"

#include "Eigen/Geometry"

// typedef boost::math::quaternion<fl> qt;
using qt = Eigen::Quaterniond;

// modified the original weird code to forward declaration.
bool quaternion_is_normalized(const qt& q);
qt angle_to_quaternion(const vec& axis,
                       fl angle);  // axis is assumed to be a unit vector
qt angle_to_quaternion(const vec& rotation);  // rotation == angle * axis
vec quaternion_to_angle(const qt& q);
mat quaternion_to_r3(const qt& q);

inline void quaternion_normalize_approx(qt& q, const fl tolerance = 1e-6) {
  const fl s = q.squaredNorm();
  assert(eq(s, sqr(q.norm())));
  if (std::abs(s - 1) < tolerance)
    ;  // most likely scenario
  else {
    const fl a = std::sqrt(s);
    assert(a > epsilon_fl);
    q.coeffs() /= a;
    assert(quaternion_is_normalized(q));
  }
}

qt random_orientation(rng& generator);
void quaternion_increment(qt& q, const vec& rotation);
vec quaternion_difference(
    const qt& b,
    const qt& a);  // rotation that needs to be applied to convert a to b
void print(const qt& q, std::ostream& out = std::cout);  // print as an angle

#endif
