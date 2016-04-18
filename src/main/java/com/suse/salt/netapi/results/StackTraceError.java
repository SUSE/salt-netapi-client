package com.suse.salt.netapi.results;

/**
 * Salt error containing a stacktrace if one is returned instead of a result
 */
public class StackTraceError implements SaltError {

   private final String stacktrace;

   public StackTraceError(String fn) {
      this.stacktrace = fn;
   }

   public String getStacktrace() {
      return stacktrace;
   }

   @Override
   public String toString() {
      return "StackTraceError(" + stacktrace + ")";
   }
}
