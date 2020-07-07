#include "random.h"

fl random_fl(fl a, fl b, rng& generator) {
  return std::uniform_real_distribution<>(a, b)(generator);
}

fl random_normal(fl mean, fl sigma, rng& generator) {
  return std::normal_distribution<>(mean, sigma)(generator);
}

int random_int(int a, int b, rng& generator) {
  return std::uniform_int_distribution<>(a, b)(generator);
}

sz random_sz(sz a, sz b, rng& generator) {
  return static_cast<sz>(
      random_int(static_cast<int>(a), static_cast<int>(b), generator));
}

vec random_inside_sphere(rng& generator) {
  while (true) {  // on average, this will have to be run about twice
    fl r1 = random_fl(-1.0, 1.0, generator);
    fl r2 = random_fl(-1.0, 1.0, generator);
    fl r3 = random_fl(-1.0, 1.0, generator);

    vec tmp(r1, r2, r3);
    if (sqr(tmp) < 1) return tmp;
  }
}

vec random_in_box(const vec& corner1, const vec& corner2,
                  rng& generator) {
  vec result;
  for (int i = 0; i < result.size(); i++) {
    result[i] = random_fl(corner1[i], corner2[i], generator);
  }
  return result;
}