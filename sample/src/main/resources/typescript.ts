/**
 * TYPES
 */
export type BigDecimal = string
/**
 * yyyy-MM-dd'T'HH:mm:ss'Z'
 */
export type LocalDateTime = Date
export type UserId = number

/**
 * ENUMS
 */
export enum UserHand {
  RIGHT = 'RIGHT',
  LEFT = 'LEFT',
}

/**
 * MODELS
 */
export interface CreateUserRequest {
  balance: BigDecimal;
  birthday?: LocalDateTime;
  dominantHand?: UserHand;
  /**
   * min: 1
   * max: 100
   * regex: [a-zA-Z]*
   */
  firstName?: string;
  /**
   * min: 1
   * max: 100
   * regex: [a-zA-Z]*
   */
  lastName?: string;
}

export interface SearchUserResponse {
  results: User[];
  totalCount: number;
}

export interface User {
  balance: BigDecimal;
  birthday?: LocalDateTime;
  dominantHand?: UserHand;
  firstName?: string;
  id: UserId;
  lastName?: string;
}
