/**
 * TYPES
 */
typedef BigDecimal = String
/**
 * yyyy-MM-dd'T'HH:mm:ss'Z'
 */
typedef LocalDateTime = String
typedef UserId = int

/**
 * ENUMS
 */
enum UserHand {
  RIGHT, // = RIGHT
  LEFT, // = LEFT
}

/**
 * MODELS
 */
class CreateUserRequest {
  final BigDecimal balance;
  final LocalDateTime? birthday;
  final UserHand? dominantHand;
  /**
   * min: 1
   * max: 100
   * regex: [a-zA-Z]*
   */
  final String? firstName;
  /**
   * min: 1
   * max: 100
   * regex: [a-zA-Z]*
   */
  final String? lastName;

  CreateUserRequest({
    required this.balance,
    required this.birthday,
    required this.dominantHand,
    required this.firstName,
    required this.lastName,
  });

}

class SearchUserResponse {
  final List<User> results;
  final int totalCount;

  SearchUserResponse({
    required this.results,
    required this.totalCount,
  });

}

class User {
  final BigDecimal balance;
  final LocalDateTime? birthday;
  final UserHand? dominantHand;
  final String? firstName;
  final UserId id;
  final String? lastName;

  User({
    required this.balance,
    required this.birthday,
    required this.dominantHand,
    required this.firstName,
    required this.id,
    required this.lastName,
  });

}
