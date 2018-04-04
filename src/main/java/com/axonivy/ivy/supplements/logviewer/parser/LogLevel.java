package com.axonivy.ivy.supplements.logviewer.parser;

public enum LogLevel
{
  // The order of the Enum entries is of importance
  TRACE("TRACE"),
  DEBUG("DEBUG"),
  INFO("INFO"),
  WARN("WARN"),
  ERROR("ERROR"),
  FATAL("FATAL");

  private String value;

  private LogLevel(String value)
  {
    this.value = value;
  }

  @Override
  public String toString()
  {
    return this.value;
  }

  /**
   * Turns a given String into the corresponding LogLevel
   * @param value e.g. "WARN"
   * @return the figured out LogLevel, DEBUG if it could not find out which one
   */
  public static LogLevel fromValue(String value)
  {
    if (value == null || "".equals(value))
    {
      return DEBUG;
    }

    for (LogLevel enumEntry : LogLevel.values())
    {
      if (enumEntry.toString().equals(value))
      {
        return enumEntry;
      }
    }

    return DEBUG;
  }
}
