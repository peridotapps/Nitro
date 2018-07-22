package com.peridotapps.nitro.string;

public enum CharacterSet {
  ALPHA,
  ALPHA_LOWER,
  ALPHA_LOWER_NUMERIC,
  ALPHA_LOWER_NUMERIC_SYMBOLS,
  ALPHA_LOWER_SYMBOLS,
  ALPHA_NUMERIC,
  ALPHA_NUMERIC_SYMBOLS,
  ALPHA_SYMBOLS,
  ALPHA_UPPER,
  ALPHA_UPPER_NUMERIC,
  ALPHA_UPPER_NUMERIC_SYMBOLS,
  ALPHA_UPPER_SYMBOLS,
  NUMERIC,
  NUMERIC_SYMBOLS,
  SYMBOLS,
  CUSTOM;
  
  private static final String CHAR_SET_ALPHA_LOWER = "abcdefghijklmnopqrstuvwxyz";
  private static final String CHAR_SET_ALPHA_UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  private static final String CHAR_SET_NUMERIC = "0123456789";
  private static final String CHAR_SET_SYMBOLS = "~`!@#$%^&*()_-+={[}]|:;'<,>.?";
  
  public String getCharacters() {
    StringBuilder charSet = new StringBuilder();
    
    if (addAlphaLower()) {
      charSet.append(CHAR_SET_ALPHA_LOWER);
    }
    
    if (addAlphaUpper()) {
      charSet.append(CHAR_SET_ALPHA_UPPER);
    }
    
    if (addNumeric()) {
      charSet.append(CHAR_SET_NUMERIC);
    }
    
    if (addSymbols()) {
      charSet.append(CHAR_SET_SYMBOLS);
    }
    
    return charSet.toString();
  }
  
  private boolean addSymbols() {
    switch (this) {
      case ALPHA_LOWER_NUMERIC_SYMBOLS:
      case ALPHA_LOWER_SYMBOLS:
      case ALPHA_NUMERIC_SYMBOLS:
      case ALPHA_SYMBOLS:
      case ALPHA_UPPER_NUMERIC_SYMBOLS:
      case ALPHA_UPPER_SYMBOLS:
      case NUMERIC_SYMBOLS:
      case SYMBOLS:
        return true;
      default:
        return false;
    }
  }
  
  private boolean addNumeric() {
    switch (this) {
      case ALPHA_LOWER_NUMERIC:
      case ALPHA_LOWER_NUMERIC_SYMBOLS:
      case ALPHA_NUMERIC:
      case ALPHA_NUMERIC_SYMBOLS:
      case ALPHA_UPPER_NUMERIC:
      case ALPHA_UPPER_NUMERIC_SYMBOLS:
      case NUMERIC:
      case NUMERIC_SYMBOLS:
        return true;
      default:
        return false;
    }
  }
  
  private boolean addAlphaUpper() {
    switch (this) {
      case ALPHA:
      case ALPHA_NUMERIC:
      case ALPHA_SYMBOLS:
      case ALPHA_NUMERIC_SYMBOLS:
      case ALPHA_UPPER:
      case ALPHA_UPPER_NUMERIC:
      case ALPHA_UPPER_NUMERIC_SYMBOLS:
      case ALPHA_UPPER_SYMBOLS:
        return true;
      default:
        return false;
    }
  }
  
  private boolean addAlphaLower() {
    switch (this) {
      case ALPHA:
      case ALPHA_LOWER:
      case ALPHA_LOWER_NUMERIC:
      case ALPHA_LOWER_NUMERIC_SYMBOLS:
      case ALPHA_LOWER_SYMBOLS:
      case ALPHA_NUMERIC:
      case ALPHA_NUMERIC_SYMBOLS:
      case ALPHA_SYMBOLS:
        return true;
      default:
        return false;
    }
  }
}
