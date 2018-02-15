package com.axonivy.ivy.supplements.logviewer.parser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class LogFileParser
{
  private File file;
  private List<LogEntry> logEntries = new ArrayList<>();

  public LogFileParser(File file)
  {
    this.file = file;
  }

  public List<LogEntry> parse() throws IOException
  {
    // TODO check if parsing like that is performant enough, else only read headers
    // TODO also support other encodings
    try (Stream<String> lines = Files.lines(file.toPath(), StandardCharsets.ISO_8859_1))
    {
      lines.forEachOrdered(line -> {

        if (!line.startsWith(" "))
        {
          createNewEntry(line);
        }
        else
        {
          appendToLastEntry(line);
        }

        // TODO check that last entry before EOF is included
      });
    }

    return logEntries;
  }

  private void createNewEntry(String line)
  {
    String[] parts = line.split(" ", 3);

    // We rearrange the priority of the title line
    LogEntry entry = null;
    if (parts.length >= 2)
    {
      entry = new LogEntry(line, parts[0], parts[1]);
    }
    else
    {
      if (!line.trim().equals("") || !parts[0].trim().equals(""))
      {
        entry = new LogEntry(line, parts[0], "DEBUG" /* we set unparsable entries to DEBUG */);
      }
    } 
    if (entry != null)
    {
      logEntries.add(entry);
    }
  }

  private void appendToLastEntry(String line)
  {
    LogEntry entry = logEntries.get(logEntries.size() - 1);
    if (entry == null)
    {
      entry = new LogEntry("Parse ERROR", "Parse Error", "Parse Error");
    }

    entry.addDetailLine(line);
  }
}
