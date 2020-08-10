#include "quaternion.h"

#include <iostream>

#include "gtest/gtest.h"

std::ostream& operator<<(std::ostream& os, const qt& q) {
  os << q.x() << "i + " << q.y() << "j + " << q.z() << "k" << " + " << q.w();
  return os;
}

TEST(QuaternionTest, Normalize) {
  qt test_qt(1.0, 2.0, 3.0, 4.0);
  std::cout << "Test Input: " << test_qt << "\n";
  EXPECT_FALSE(quaternion_is_normalized(test_qt));

  quaternion_normalize_approx(test_qt);
  std::cout << "Test Input (Normalized): " << test_qt << "\n";
  EXPECT_NEAR(GetA(test_qt), 0.18257418583505536, 1e-9);
  EXPECT_NEAR(GetB(test_qt), 0.36514837167011072, 1e-9);
  EXPECT_NEAR(GetC(test_qt), 0.54772255750516607, 1e-9);
  EXPECT_NEAR(GetD(test_qt), 0.73029674334022143, 1e-9);

  EXPECT_TRUE(quaternion_is_normalized(test_qt));
}

TEST(QuaternionTest, Identity) {
  qt test_qt = qt::Identity();
  std::cout << "Test Input: " << test_qt << "\n";
  EXPECT_TRUE(quaternion_is_normalized(test_qt));

  EXPECT_DOUBLE_EQ(GetA(test_qt), 1.0);
  EXPECT_DOUBLE_EQ(GetB(test_qt), 0.0);
  EXPECT_DOUBLE_EQ(GetC(test_qt), 0.0);
  EXPECT_DOUBLE_EQ(GetD(test_qt), 0.0);

  // Tests SetIdentity
  test_qt.x() = 2.0;
  test_qt.z() = 3.0;
  test_qt.setIdentity();
  EXPECT_DOUBLE_EQ(GetA(test_qt), 1.0);
  EXPECT_DOUBLE_EQ(GetB(test_qt), 0.0);
  EXPECT_DOUBLE_EQ(GetC(test_qt), 0.0);
  EXPECT_DOUBLE_EQ(GetD(test_qt), 0.0);
}

TEST(QuaternionTest, AngleToQuaternion) {
  const vec test_axis(0.324442, 0.486664, 0.811107);
  const fl test_angle = 0.7;
  qt result = angle_to_quaternion(test_axis, test_angle);
  std::cout << "Test Input: " << result << "\n";
  EXPECT_TRUE(quaternion_is_normalized(result));

  EXPECT_NEAR(GetA(result), 0.93937271284737889, 1e-9);
  EXPECT_NEAR(GetB(result), 0.11125045044646155, 1e-9);
  EXPECT_NEAR(GetC(result), 0.1668760185674997, 1e-9);
  EXPECT_NEAR(GetD(result), 0.2781268119117688, 1e-9);

  const vec inverse_angle = quaternion_to_angle(result);
  EXPECT_NEAR(inverse_angle[0], 0.22710939999, 1e-9);
  EXPECT_NEAR(inverse_angle[1], 0.340664799999, 1e-9);
  EXPECT_NEAR(inverse_angle[2], 0.5677749, 1e-9);
}

TEST(QuaternionTest, ToRotationMatrix) {
  qt test_qt(1.0, 2.0, 3.0, 4.0);
  std::cout << "Test Input: " << test_qt << "\n";
  EXPECT_FALSE(quaternion_is_normalized(test_qt));

  quaternion_normalize_approx(test_qt);
  std::cout << "Test Input (Normalized): " << test_qt << "\n";

  const mat rotation = quaternion_to_r3(test_qt);

  EXPECT_NEAR(rotation(0, 0), -0.66666666666666652, 1e-9);
  EXPECT_NEAR(rotation(0, 1), 0.13333333333333336, 1e-9);
  EXPECT_NEAR(rotation(0, 2), 0.73333333333333317, 1e-9);
  EXPECT_NEAR(rotation(1, 0), 0.66666666666666652, 1e-9);
  EXPECT_NEAR(rotation(1, 1), -0.33333333333333326, 1e-9);
  EXPECT_NEAR(rotation(1, 2), 0.66666666666666663, 1e-9);
  EXPECT_NEAR(rotation(2, 0), 0.3333333333333332, 1e-9);
  EXPECT_NEAR(rotation(2, 1), 0.93333333333333324, 1e-9);
  EXPECT_NEAR(rotation(2, 2), 0.1333333333333333, 1e-9);
}

TEST(QuaternionTest, RandomOrientation) {
  rng generator(1);
  qt random_qt = random_orientation(generator);
  std::cout << "Random Quaternion: " << random_qt << "\n";
  EXPECT_TRUE(quaternion_is_normalized(random_qt));
}

TEST(QuaternionTest, QuaternionIncrement) {
  qt test_qt(1.0, 2.0, 3.0, 4.0);
  std::cout << "Test Input: " << test_qt << "\n";
  EXPECT_FALSE(quaternion_is_normalized(test_qt));

  quaternion_normalize_approx(test_qt);
  std::cout << "Test Input (Normalized): " << test_qt << "\n";

  const vec rotation(0.2, 0.3, 0.5);

  quaternion_increment(test_qt, rotation);
  EXPECT_NEAR(GetA(test_qt), -0.12252991069103984, 1e-9);
  EXPECT_NEAR(GetB(test_qt),  0.33895584453796018, 1e-9);
  EXPECT_NEAR(GetC(test_qt), 0.56683533339894421, 1e-9);
  EXPECT_NEAR(GetD(test_qt), 0.74080568386730927, 1e-9);
  std::cout << "Test Input (Incremented): " << test_qt << "\n";
}

TEST(QuaternionTest, QuaternionDifference) {
  qt test_a(1.0, 2.0, 3.0, 4.0);
  std::cout << "Test Input a: " << test_a << "\n";
  EXPECT_FALSE(quaternion_is_normalized(test_a));

  quaternion_normalize_approx(test_a);
  std::cout << "Test Input a (Normalized): " << test_a << "\n";

  qt test_b(2.0, 3.0, 4.0, 5.0);
  std::cout << "Test Input b: " << test_b << "\n";
  EXPECT_FALSE(quaternion_is_normalized(test_b));

  quaternion_normalize_approx(test_b);
  std::cout << "Test Input b (Normalized): " << test_b << "\n";

  const vec rotation = quaternion_difference(test_a, test_b);
  EXPECT_NEAR(rotation.data[0], 0.099586430700069686, 1e-9);
  EXPECT_NEAR(rotation.data[1], 0.0, 1e-9);
  EXPECT_NEAR(rotation.data[2], 0.19917286140013937, 1e-9);
}
