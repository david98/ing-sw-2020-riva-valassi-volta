package it.polimi.vovarini.view.cli;

import com.sun.jna.Function;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.io.Reader;
import java.util.Scanner;

import static com.sun.jna.platform.win32.Wincon.ENABLE_LINE_INPUT;

public class FullScreenConsole implements Console {

  private int printedLineCount;

  private final Terminal terminal;
  private final Reader reader;

  public FullScreenConsole() throws IOException {
    printedLineCount = 0;

    if(System.getProperty("os.name").startsWith("Windows"))
    {
      // Set output mode to handle virtual terminal sequences
      Function GetStdHandleFunc = Function.getFunction("kernel32", "GetStdHandle");
      WinDef.DWORD STD_OUTPUT_HANDLE = new WinDef.DWORD(-11);
      WinNT.HANDLE hOut = (WinNT.HANDLE)GetStdHandleFunc.invoke(WinNT.HANDLE.class, new Object[]{STD_OUTPUT_HANDLE});

      WinDef.DWORDByReference p_dwMode = new WinDef.DWORDByReference(new WinDef.DWORD(0));
      Function GetConsoleModeFunc = Function.getFunction("kernel32", "GetConsoleMode");
      GetConsoleModeFunc.invoke(WinDef.BOOL.class, new Object[]{hOut, p_dwMode});

      int ENABLE_VIRTUAL_TERMINAL_PROCESSING = 4;
      WinDef.DWORD dwMode = p_dwMode.getValue();
      dwMode.setValue((dwMode.intValue() | ENABLE_VIRTUAL_TERMINAL_PROCESSING) &~ENABLE_LINE_INPUT);
      Function SetConsoleModeFunc = Function.getFunction("kernel32", "SetConsoleMode");
      SetConsoleModeFunc.invoke(WinDef.BOOL.class, new Object[]{hOut, dwMode});
    }

    terminal = TerminalBuilder.builder()
            .jna(true)
            .system(true)
            .build();

    reader = terminal.reader();
  }

  @Override
  public void clear() {
    for (int i = 0; i < printedLineCount; i++) {
      System.out.print("\033[F\r");
    }
    System.out.print("\033[H\033[2J");
    System.out.flush();
    printedLineCount = 0;
  }

  @Override
  public void print(String str) {
    System.out.print(str);
    long newLines = str.chars().filter(ch -> ch == '\n').count();
    printedLineCount += newLines + 1;
  }

  @Override
  public void println(String str) {
    print(str + "\n");
  }

  @Override
  public Reader getReader() {
    return reader;
  }

  @Override
  public Scanner getScanner() {
    return new Scanner(System.in);
  }

  @Override
  public void enterRawMode() {
    terminal.enterRawMode();
  }
}
