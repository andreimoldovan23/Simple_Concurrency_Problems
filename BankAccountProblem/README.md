# Bank transactions simulator

- multiple bank accounts are generated with an id, initial sum, current sum and a list of transactions

- multiple threads perform transactions at the same time on the list of bank accounts

- after each transaction is backlogged the balance is checked and if anything went wrong the last transaction is undone

- all thrown exceptions are propagated into the main thread
