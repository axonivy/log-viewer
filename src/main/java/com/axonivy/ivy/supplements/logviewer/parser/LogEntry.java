package com.axonivy.ivy.supplements.logviewer.parser;

public class LogEntry
{
  private String originalTitleLine;
  private String time;
  private LogLevel severity;
  private String details;

  public LogEntry(String originalTitleLine, String time, LogLevel severity)
  {
    this.originalTitleLine = originalTitleLine;
    this.time = time;
    this.severity = severity;
  }

  public void addDetailLine(String detailLine)
  {
    if (details == null)
    {
      details = detailLine;
    }
    else
    {
      details += "\n" + detailLine;
    }
  }

  public String getOriginalTitleLine()
  {
    return originalTitleLine;
  }

  public String getTitleLine()
  {
    String detailTitle = "";
    if (details != null)
    {
      detailTitle = details.split("[\\r\\n]+")[0];
    }
    return time + " " + severity + ": " + detailTitle.trim();
  }

  public String getTime()
  {
    return time;
  }

  public LogLevel getSeverity()
  {
    return severity;
  }

  public String getDetails()
  {
    return originalTitleLine + "\n" + details;
  }
  
  @Override
  public String toString()
  {
    return getTitleLine();
  }
}
