/**
 * TYPES
 */
typealias BigDecimal = Decimal
/**
 * yyyy-MM-dd'T'HH:mm:ss'Z'
 */
typealias LocalDateTime = String
typealias UserId = Int

/**
 * ENUMS
 */
enum UserHand: String, Codable {
  case right = "RIGHT"
  case left = "LEFT"
}

/**
 * MODELS
 */
struct CreateUserRequest: Codable {
  let balance: BigDecimal
  let birthday: LocalDateTime?
  let dominantHand: UserHand?
  /**
   * min: 1
   * max: 100
   * regex: [a-zA-Z]*
   */
  let firstName: String?
  /**
   * min: 1
   * max: 100
   * regex: [a-zA-Z]*
   */
  let lastName: String?
}

struct SearchUserResponse: Codable {
  let results: [User]
  let totalCount: Int
}

struct User: Codable {
  let balance: BigDecimal
  let birthday: LocalDateTime?
  let dominantHand: UserHand?
  let firstName: String?
  let id: UserId
  let lastName: String?
}
