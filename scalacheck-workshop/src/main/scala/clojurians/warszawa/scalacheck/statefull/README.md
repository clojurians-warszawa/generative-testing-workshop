0. Run `CandyMachineProperties` and see in the console that the test passes.
1. Adjust `CandyMachineSpecification.newSut` so that the "Maintains invariants." property holds.
2. Implement `case class InsertCoin() extends UnitCommand` similarly to `ChooseProduct`
  and adjust `CandyMachineSpecification.genCommand` so that both `InsertCoin` and `ChooseProduct` Commands are invoked.

3. a) Implement your first `Prop`erty in `CandyMachineSpecification.invariants(state: State)`

```state.machineState.delivered.products.map(_.value).sum == state.machineState.internalPocket.coins.size```
   You should see sth like:

   ! CommandsLevelDB.Never breaks.: Falsified after 7 passed tests.
> Labels of failing property:
initialstate = State(MachineState(Pocket(List()),Pocket(List()),Pocket(List
  ()),DeliveryBox(List()),Products(Map(1 -> Product(Coke,3), 2 -> Product(P
  epsi,2))),None))
seqcmds = (ChooseProduct(1); InsertCoin(); InsertCoin(); InsertCoin(); Choo
  seProduct(1))
> ARG_0: Actions(State(MachineState(Pocket(List()),Pocket(List()),Pocket(Li
  st()),DeliveryBox(List()),Products(Map(1 -> Product(Coke,3), 2 -> Product
  (Pepsi,2))),None)),List(ChooseProduct(1), InsertCoin(), InsertCoin(), Ins
  ertCoin(), ChooseProduct(1)),List())
> ARG_0_ORIGINAL: Actions(State(MachineState(Pocket(List()),Pocket(List()),
  Pocket(List()),DeliveryBox(List()),Products(Map(1 -> Product(Coke,3), 2 -
  > Product(Pepsi,2))),None)),List(InsertCoin(), ChooseProduct(1), InsertCo
  in(), InsertCoin(), InsertCoin(), ChooseProduct(1), InsertCoin()),List())

  Note the ARG_0_ORIGINAL counter-example is longer then the shrinked to minimum ARG_0 counter-example

   b) fix the implementation of `Machine.releaseProduct()` method

 4. a) Implement your second `Prop`:
```state.machineState.products.map.size + state.machineState.delivered.products.size == initialProducts.size```
    Don't forget to label your new invariant (see `Labeling Properties` at https://github.com/rickynils/scalacheck/wiki/User-Guide)

    b) fix the implementation of `Machine.releaseProduct()` method
    c) you should now see sth like:
```
    ! CommandsLevelDB.Invariants maintained.: Exception raised on property evaluation.
    > Exception: java.util.NoSuchElementException: key not found: 1
```


5. Fix `Machine.releaseProduct()` and `Machine.chooseProduct()`

6. So far we have been generating the commands in a somewhat naive way. Let's change that!
a) modify `genCommand` in such a way that choosing Product 2 is possible.
b) fix implementation
c) Create a generator for a `Product` with name being alpha characters and value being a positive integer lower then 10
d) Use this generator to generate `initialProducts`
  Hint: you will need to modify the `CandyMachineSpecificationState` class
e) modify `genCommand` so that any product from `initialProducts` can be chosen.
f) use `Gen.frequency` to generate 5 times more insertCoin commands then chooseProduct commands.

7. change number of run tests to 10000 

8. [optional] add property verifying that number of coins in the environment does not change (hint: add "nInsertedCoins: Int" to state)
