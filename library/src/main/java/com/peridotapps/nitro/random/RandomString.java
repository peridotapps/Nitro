package com.peridotapps.nitro.random;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.peridotapps.nitro.string.CharacterSet;
import java.util.Random;

import static com.peridotapps.nitro.string.CharacterSet.ALPHA_NUMERIC;
import static com.peridotapps.nitro.string.CharacterSet.CUSTOM;

public final class RandomString extends Randomizer<String> {
  
  private static final int DEFAULT_RANDOM_STRING_MAX_LENGTH = 50;
  private static final int DEFAULT_RANDOM_STRING_MIN_LENGTH = 1;
  private static final CharacterSet DEFAULT_RANDOM_STRING_CHARACTER_SET = ALPHA_NUMERIC;
  
  private int maximumLength = DEFAULT_RANDOM_STRING_MAX_LENGTH;
  private int minimumLength = DEFAULT_RANDOM_STRING_MIN_LENGTH;
  private CharacterSet characterSet = DEFAULT_RANDOM_STRING_CHARACTER_SET;
  private String customCharacterSet = "";
  
  public RandomString(CharacterSet characterSetType) {
    this(DEFAULT_RANDOM_STRING_MAX_LENGTH, characterSetType);
  }
  
  public RandomString(int maximumLength, CharacterSet characterSetType) {
    this(DEFAULT_RANDOM_STRING_MIN_LENGTH, maximumLength, characterSetType);
  }
  
  public RandomString(int minimumLength, int maximumLength, CharacterSet characterSetType) {
    this(minimumLength, maximumLength);
    this.characterSet = characterSetType;
  }
  
  public RandomString(int minimumLength, int maximumLength) {
    this(maximumLength);
    this.minimumLength = minimumLength;
  }
  
  public RandomString(int maximumLength) {
    this();
    this.maximumLength = maximumLength;
  }
  
  public RandomString() {
    characterSet = DEFAULT_RANDOM_STRING_CHARACTER_SET;
  }
  
  @Override
  @Nullable
  protected String onGenerate(Random r) {
    StringBuilder randomStringBuilder = new StringBuilder();
    
    String chars = getCharacterSetCharacters();
    Integer length = new RandomInteger(this.minimumLength, this.maximumLength).generate();
    
    if (length != null) {
      for (int index = 0; index < length; index++) {
        Integer charIndex = new RandomInteger(chars.length()).generate();
        if (charIndex != null) {
          randomStringBuilder.append(chars.charAt(charIndex));
        }
      }
    } else {
      return null;
    }
    
    return randomStringBuilder.toString();
  }
  
  public final RandomString setCharacterSet(CharacterSet characterSetType) {
    if (characterSetType != CUSTOM) {
      this.customCharacterSet = "";
      this.characterSet = characterSetType;
    }
    
    return this;
  }
  
  public final RandomString setCharacterSet(String customCharacterSet) {
    if (!TextUtils.isEmpty(customCharacterSet)) {
      this.customCharacterSet = customCharacterSet;
      this.characterSet = CUSTOM;
    }
    return this;
  }
  
  public final RandomString setMinLength(int minimumLength) {
    this.minimumLength = (minimumLength > 0)
        ? minimumLength
        : 1;
    return this;
  }
  
  public final RandomString setMaxLength(int maximumLength) {
    this.maximumLength = (maximumLength > 0)
        ? maximumLength
        : 1;
    return this;
  }
  
  public int getMinimumLength() {
    return minimumLength;
  }
  
  public int getMaximumLength() {
    return maximumLength;
  }
  
  public CharacterSet getCharacterSet() {
    return characterSet;
  }
  
  public String getCharacterSetCharacters() {
    return (TextUtils.isEmpty(customCharacterSet))
        ? (getCharacterSet() != CUSTOM)
        ? getCharacterSet().getCharacters()
        : ALPHA_NUMERIC.getCharacters()
        : customCharacterSet;
  }
}
