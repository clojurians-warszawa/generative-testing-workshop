Inspect `FuturesFailure` to find the (pretty obvious) race condition

Your task is to create a test for `FuturesFailure` using ScalaCheck excavating this race condition.

To this end we need to:
1. Implement an ExecutionContext in `FuturesFailureSpecification`.
The ExecutionContext should shuffle the incoming Futures to make sure that all interleavings are possible
2. Replace the `executionContextGen` with one using the ExecutionContext you implemented.
3. We still need to better understand what went wrong. Can we store the stacktrace of each Future invoked
  and present it later to the user?
3. The last mising part is a decent Shrinking process. See org.scalacheck.Shrink for examples of Shrinks.


