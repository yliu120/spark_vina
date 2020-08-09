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

#include "quaternion.h"

bool quaternion_is_normalized(
    const qt& q) {  // not in the interface, used in assertions
  // return eq(q.squaredNorm(), 1.0) && eq(boost::math::abs(q), 1.0);
  return eq(q.norm(), 1.0);
}

qt angle_to_quaternion(const vec& axis,
                       fl angle) {  // axis is assumed to be a unit vector
  // assert(eq(tvmet::norm2(axis), 1));
  assert(eq(axis.norm(), 1));
  normalize_angle(
      angle);  // this is probably only necessary if angles can be very big
  fl c = std::cos(angle / 2);
  fl s = std::sin(angle / 2);
  return qt(c, s * axis[0], s * axis[1], s * axis[2]);
}

qt angle_to_quaternion(const vec& rotation) {
  fl angle = rotation.norm();
  if (angle > epsilon_fl) {
    vec axis = (1 / angle) * rotation;
    return angle_to_quaternion(axis, angle);
  }
  return qt::Identity();
}

vec quaternion_to_angle(const qt& q) {
  assert(quaternion_is_normalized(q));
  const fl c = q.x();
  if (c > -1 && c < 1) {  // c may in theory be outside [-1, 1] even with
                          // approximately normalized q, due to rounding errors
    fl angle = 2 * std::acos(c);      // acos is in [0, pi]
    if (angle > pi) angle -= 2 * pi;  // now angle is in [-pi, pi]
    vec axis(q.y(), q.z(), q.w());
    // perhaps not very efficient to calculate sin of acos
    fl s = std::sin(angle / 2);
    if (std::abs(s) < epsilon_fl) return zero_vec;
    axis *= (angle / s);
    return axis;
  } else  // when c = -1 or 1, angle/2 = 0 or pi, therefore angle = 0
    return zero_vec;
}

mat quaternion_to_r3(const qt& q) {
  assert(quaternion_is_normalized(q));

  const fl a = q.x();
  const fl b = q.y();
  const fl c = q.z();
  const fl d = q.w();

  const fl aa = a * a;
  const fl ab = a * b;
  const fl ac = a * c;
  const fl ad = a * d;
  const fl bb = b * b;
  const fl bc = b * c;
  const fl bd = b * d;
  const fl cc = c * c;
  const fl cd = c * d;
  const fl dd = d * d;

  assert(eq(aa + bb + cc + dd, 1));

  mat tmp;

  // from http://www.boost.org/doc/libs/1_35_0/libs/math/quaternion/TQE.pdf
  tmp(0, 0) = (aa + bb - cc - dd);
  tmp(0, 1) = 2 * (-ad + bc);
  tmp(0, 2) = 2 * (ac + bd);

  tmp(1, 0) = 2 * (ad + bc);
  tmp(1, 1) = (aa - bb + cc - dd);
  tmp(1, 2) = 2 * (-ab + cd);

  tmp(2, 0) = 2 * (-ac + bd);
  tmp(2, 1) = 2 * (ab + cd);
  tmp(2, 2) = (aa - bb - cc + dd);

  return tmp;
}

qt random_orientation(rng& generator) {
  qt q(random_normal(0, 1, generator), random_normal(0, 1, generator),
       random_normal(0, 1, generator), random_normal(0, 1, generator));
  fl nrm = q.norm();
  if (nrm > epsilon_fl) {
    q.coeffs() /= nrm;
    assert(quaternion_is_normalized(q));
    return q;
  } else
  // this call should almost never happen
  return random_orientation(generator);  
}

void quaternion_increment(qt& q, const vec& rotation) {
  assert(quaternion_is_normalized(q));
  q = angle_to_quaternion(rotation) * q;
  quaternion_normalize_approx(q);
}

vec quaternion_difference(
    const qt& b,
    const qt& a) {  // rotation that needs to be applied to convert a to b
  quaternion_is_normalized(a);
  quaternion_is_normalized(b);
  // b = tmp * a    =>   b * inv(a) = tmp
  return quaternion_to_angle(b * a.inverse());
}

void print(const qt& q, std::ostream& out) {  // print as an angle
  print(quaternion_to_angle(q), out);
}

fl GetA(const qt& q) { return q.a(); }
fl GetB(const qt& q) { return q.b(); }
fl GetC(const qt& q) { return q.c(); }
fl GetD(const qt& q) { return q.d(); }
