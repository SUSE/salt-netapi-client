package com.suse.salt.netapi.results;

/**
 * Salt error when trying to execute a function that does not exist
 */
public class FunctionNotAvailable implements SaltError {

   private final String functionName;

   public FunctionNotAvailable(String fn) {
      this.functionName = fn;
   }

   public String getFunctionName() {
      return functionName;
   }

   @Override
   public String toString() {
      return "FunctionNotAvailable(" + functionName + ")";
   }
}
