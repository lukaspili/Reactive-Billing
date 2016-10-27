package com.github.lukaspili.reactivebilling;

public interface Logger {
  void log(String message);

  Logger DEFAULT = new Logger() {
    @Override public void log(String message) {
      // no op
    }
  };
}
