# CSE 111: Object-Oriented Programming (Java) — Midterm Master Guide

A concise, high-yield revision guide covering all core midterm concepts, runnable code implementations, in-depth memory explanations, and an exhaustive catalog of classic exam bugs, pitfalls, and traps.

---

## Quick Navigation

1. [Core Architecture: Design Class vs. Driver Class](#1-core-architecture-design-class-vs-driver-class)
2. [Instance Variables vs. Local Variables](#2-instance-variables-vs-local-variables)
3. [Constructors & Constructor Overloading](#3-constructors--constructor-overloading)
4. [Instance Methods & Method Overloading](#4-instance-methods--method-overloading)
5. [Access Modifiers & Encapsulation](#5-access-modifiers--encapsulation)
6. [Reference Passing, Memory & Aliasing](#6-reference-passing-memory--aliasing)
7. [Unified All-In-One Code Example & Walkthrough](#7-unified-all-in-one-code-example--walkthrough)
8. [Comprehensive Pitfalls, Bugs & Anti-Patterns Catalog](#8-comprehensive-pitfalls-bugs--anti-patterns-catalog)
   * [Group A: Constructor Pitfalls](#group-a-constructor-pitfalls)
   * [Group B: Overloading & Method Signature Traps](#group-b-overloading--method-signature-traps)
   * [Group C: Scope, Shadowing & Variable Traps](#group-c-scope-shadowing--variable-traps)
   * [Group D: References, Aliasing & Memory Traps](#group-d-references-aliasing--memory-traps)
   * [Group E: Static vs. Instance & Access Traps](#group-e-static-vs-instance--access-traps)
9. [Exam Output-Trace Survival Rules](#9-exam-output-trace-survival-rules)

---

## 1. Core Architecture: Design Class vs. Driver Class

Object-Oriented Programming models software as interacting **objects** that encapsulate **state** (attributes) and **behavior** (methods).

* **Design Class**: The blueprint / template modeling an entity. Defines instance variables and instance methods. It **does not** contain `main()`.
* **Driver / Tester Class**: The execution harness containing `public static void main(String[] args)`. It instantiates objects from design classes and controls execution flow.

```
+-------------------------------------------------------------+
| STACK MEMORY (Thread-specific frames)                       |
|                                                             |
| main() frame:                                               |
|   Student s1 = 0x7F2A  --------------------+                |
|   Student s2 = 0x7F2A  (Alias to s1!)      |                |
|   int x = 10                               |                |
+--------------------------------------------|----------------+
                                             v
+--------------------------------------------|----------------+
| HEAP MEMORY (Shared dynamic objects)       |                |
|                                            v                |
|   [0x7F2A] Student Object Instance <-------+                |
|     |-- name: "Alice"                                       |
|     |-- id: 101                                             |
|     +-- cgpa: 3.85                                          |
+-------------------------------------------------------------+
```

### Clean Implementation
```java
// DESIGN CLASS (No main method)
class Student {
    String name;
    int id;
    double cgpa;

    void printDetails() {
        System.out.println("ID: " + id + " | Name: " + name + " | CGPA: " + cgpa);
    }
}

// DRIVER CLASS (Contains main)
public class Main {
    public static void main(String[] args) {
        Student s1 = new Student(); // 's1' lives on Stack; actual object lives on Heap
        s1.name = "Alice";
        s1.id = 101;
        s1.cgpa = 3.85;
        s1.printDetails();
    }
}
```

---

## 2. Instance Variables vs. Local Variables

| Aspect | Instance Variable | Local Variable |
| :--- | :--- | :--- |
| **Location** | Inside class, outside any method or block. | Inside a method, constructor, or `{ }` block. |
| **Default Values** | **Auto-initialized** (`0`, `0.0`, `false`, `null`). | **No default values**. Must be explicitly initialized. |
| **Scope** | Throughout the entire class (subject to access modifier). | Only within the enclosing `{ }` block. |
| **Lifecycle** | Created when object is allocated (`new`); destroyed by GC. | Created when block is executed; destroyed on exit. |
| **Memory** | Stored on the **Heap** inside the object instance. | Stored on the **Stack** in the method's frame. |

### Code Comparison
```java
class ScopeDemo {
    int instanceCount;      // Auto-initialized to 0
    String instanceName;    // Auto-initialized to null
    boolean isAvailable;    // Auto-initialized to false

    void compute(int factor) { // 'factor' is a local parameter
        int localTotal = 0;    // Local variable: MUST initialize before reading!

        // int invalid;
        // System.out.println(invalid); // ❌ COMPILE ERROR: variable might not have been initialized

        localTotal = this.instanceCount * factor;
        System.out.println("Computed: " + localTotal);
    }
}
```

---

## 3. Constructors & Constructor Overloading

A **constructor** is a dedicated initialization block invoked automatically upon `new`.

### Golden Rules
1. Must have the **exact same name** as the enclosing class.
2. Must have **no return type** (not even `void`).
3. If no constructor is written, compiler injects an invisible **default no-arg constructor**.
4. **The Vanishing Default Constructor Rule**: If you define *any* constructor with parameters, Java permanently removes the default no-arg constructor!
5. **Constructor Chaining (`this(...)`)**: Allows one constructor to delegate to another to avoid redundant code. **`this(...)` must be the very first line**.

### Code Example: Constructor Chaining
```java
class Laptop {
    String brand;
    int ramGB;
    double price;

    // 1. Default constructor
    public Laptop() {
        this("Generic", 8, 500.0); // Chains to constructor #3
    }

    // 2. Overloaded constructor (1 parameter)
    public Laptop(String brand) {
        this(brand, 8, 650.0);     // Chains to constructor #3
    }

    // 3. Master constructor (3 parameters)
    public Laptop(String brand, int ramGB, double price) {
        this.brand = brand;
        this.ramGB = ramGB;
        this.price = price;
    }
}
```

---

## 4. Instance Methods & Method Overloading

### Instance Methods
* Invoked on a specific instance: `objectReference.methodName(arguments)`.
* Have access to the implicit `this` reference representing the calling instance.

### Method Overloading Rules
Two or more methods in the **same class** share the **same name**, differentiated by parameter signatures.

| What Differentiates Overloads | What DOES NOT Differentiate Overloads |
| :--- | :--- |
| **Number** of parameters (`int` vs `int, int`) | **Return type** (`int foo()` vs `void foo()`) |
| **Types** of parameters (`int` vs `double`) | **Parameter names** (`foo(int x)` vs `foo(int y)`) |
| **Order** of parameter types (`int, String` vs `String, int`) | **Access modifiers** (`public` vs `private`) |

```java
class Printer {
    public void print(String text) {
        System.out.println("String: " + text);
    }

    // Valid: different type
    public void print(int number) {
        System.out.println("Integer: " + number);
    }

    // Valid: different parameter count
    public void print(String text, int copies) {
        for (int i = 0; i < copies; i++) System.out.println(text);
    }

    // Valid: different type ordering
    public void print(int copies, String text) {
        print(text, copies); // Reuses logic
    }
}
```

---

## 5. Access Modifiers & Encapsulation

### Access Modifiers Matrix
| Modifier | Same Class | Same Package | Subclass (outside pkg) | Universal (anywhere) |
| :--- | :---: | :---: | :---: | :---: |
| `private` | **Yes** | No | No | No |
| *(default / package-private)* | **Yes** | **Yes** | No | No |
| `protected` | **Yes** | **Yes** | **Yes** | No |
| `public` | **Yes** | **Yes** | **Yes** | **Yes** |

### Encapsulation (Data Hiding)
Encapsulation safeguards internal representation by bundling state and behavior, restricting direct field manipulation:
1. Mark instance variables `private`.
2. Provide `public` getters (read) and setters (write).
3. Validate invariants inside setters to reject corrupted or invalid state.

```java
class Account {
    private double balance; // Data hiding

    public double getBalance() {
        return this.balance;
    }

    public void setBalance(double balance) {
        if (balance >= 0) {
            this.balance = balance; // Invariant enforced
        } else {
            System.out.println("Invalid negative balance rejected.");
        }
    }
}
```

---

## 6. Reference Passing, Memory & Aliasing

> [!IMPORTANT]
> **Java is strictly 100% PASS-BY-VALUE**.
> There is **no pass-by-reference** in Java. When an object is passed into a method, Java copies the **memory address value** of the reference variable into the method parameter.

### Mutation vs. Reassignment
* **Mutating State (`param.setField(...)`)**: Follows the copied pointer to the Heap and modifies the actual object. **Affects caller**.
* **Reassigning Reference (`param = new Class(...)`)**: Overwrites the local parameter pointer on the Stack with a new address. **Does NOT affect caller**.

### Aliasing
When multiple references hold the exact same memory address (`s2 = s1;`), modifying fields through `s2` immediately reflects when inspected via `s1`.

```
Step 1: Student s1 = new Student("Bob");
  Stack: s1 = 0x100  -->  Heap: [0x100] name = "Bob"

Step 2: Student s2 = s1; (Aliasing)
  Stack: s1 = 0x100  --\
  Stack: s2 = 0x100  ------> Heap: [0x100] name = "Bob"

Step 3: s2.setName("Alice");
  Heap: [0x100] name mutated to "Alice"
  s1.getName() now yields "Alice"!
```

---

## 7. Unified All-In-One Code Example & Walkthrough

The program below incorporates every single midterm topic in one cohesive, runnable file.

```java
// ============================================================================
// DESIGN CLASS: BankAccount
// Demonstrates: Encapsulation, Variable Shadowing, Overloaded Constructors,
//               Constructor Chaining, Instance Methods, Method Overloading
// ============================================================================
class BankAccount {
    // 1. Private Instance Variables (Encapsulation / Data Hiding)
    private int accountNumber;
    private String holderName;
    private double balance;

    // 2. Default / No-arg Constructor
    public BankAccount() {
        this(0, "Unknown", 0.0); // Constructor chaining using this(...)
    }

    // 3. Overloaded Constructor (2 arguments)
    public BankAccount(int accountNumber, String holderName) {
        this(accountNumber, holderName, 0.0); // Chains to 3-arg constructor
    }

    // 4. Overloaded Master Constructor (3 arguments)
    public BankAccount(int accountNumber, String holderName, double balance) {
        // Variable shadowing resolved explicitly via 'this.'
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        setBalance(balance); // Invokes setter to enforce data validation
    }

    // 5. Getters and Setters
    public int getAccountNumber() { return this.accountNumber; }
    public String getHolderName() { return this.holderName; }

    public void setHolderName(String holderName) {
        if (holderName != null && !holderName.trim().isEmpty()) {
            this.holderName = holderName;
        }
    }

    public double getBalance() { return this.balance; }

    public void setBalance(double balance) {
        if (balance >= 0) {
            this.balance = balance;
        } else {
            System.out.println("[Error] Negative balance rejected: " + balance);
        }
    }

    // 6. Base Instance Method
    public void deposit(double amount) {
        if (amount > 0) {
            this.balance += amount;
        }
    }

    // 7. Overloaded Method 1: Deposit with fee deduction
    public void deposit(double amount, double fee) {
        if (amount > fee && fee >= 0) {
            this.balance += (amount - fee);
        }
    }

    // 8. Overloaded Method 2: Deposit by check verification
    public void deposit(String checkNumber, double amount) {
        System.out.println("Clearing check #" + checkNumber);
        deposit(amount); // Reuses primary deposit logic
    }

    public void displayAccount() {
        System.out.println("Acc #" + accountNumber + " | Holder: " + holderName + " | Balance: $" + balance);
    }
}

// ============================================================================
// DRIVER CLASS: Main
// Demonstrates: Object Creation, Parameter Passing (Mutation vs Reassignment),
//               Aliasing, Method Invocations
// ============================================================================
public class Main {

    // Demonstrates pass-by-value reference behavior
    public static void testReferences(BankAccount mutateTarget, BankAccount reassignTarget) {
        // MUTATION: Dereferences pointer to modify Heap state (Caller affected!)
        mutateTarget.setBalance(9999.0);

        // REASSIGNMENT: Overwrites only the local Stack pointer copy (Caller NOT affected!)
        reassignTarget = new BankAccount(999, "Ghost", 50000.0);
    }

    public static void main(String[] args) {
        System.out.println("=== 1. Constructors & Chaining ===");
        BankAccount acc1 = new BankAccount();
        BankAccount acc2 = new BankAccount(101, "Alice");
        BankAccount acc3 = new BankAccount(102, "Bob", 1500.0);

        acc1.displayAccount(); // Acc #0 | Holder: Unknown | Balance: $0.0
        acc2.displayAccount(); // Acc #101 | Holder: Alice | Balance: $0.0
        acc3.displayAccount(); // Acc #102 | Holder: Bob | Balance: $1500.0

        System.out.println("\n=== 2. Encapsulation & Validation ===");
        acc2.setBalance(-500.0); // Rejected by invariant validation rule
        acc2.setBalance(350.0);   // Accepted
        acc2.displayAccount();    // Balance: $350.0

        System.out.println("\n=== 3. Method Overloading ===");
        acc3.deposit(500.0);             // Matches deposit(double)
        acc3.deposit(200.0, 15.0);       // Matches deposit(double, double) -> net +$185
        acc3.deposit("CHK-4491", 300.0); // Matches deposit(String, double) -> net +$300
        acc3.displayAccount();           // Balance: $1500 + 500 + 185 + 300 = $2485.0

        System.out.println("\n=== 4. Reference Passing (Mutation vs Reassignment) ===");
        BankAccount orig1 = new BankAccount(201, "Charlie", 100.0);
        BankAccount orig2 = new BankAccount(202, "Dave", 200.0);

        testReferences(orig1, orig2);

        orig1.displayAccount(); // Balance: $9999.0 (MUTATED!)
        orig2.displayAccount(); // Balance: $200.0 (Dave is UNCHANGED!)

        System.out.println("\n=== 5. Aliasing ===");
        BankAccount alias = orig1; // Both references point to same address in heap
        alias.setHolderName("Charlie-Updated");
        System.out.println("orig1 name after alias update: " + orig1.getHolderName());
    }
}
```

---

## 8. Comprehensive Pitfalls, Bugs & Anti-Patterns Catalog

A curated breakdown of the most common compilation errors, runtime exceptions, and silent logic traps encountered in CSE 111 exams.

```
       +-----------------------------------------------------------+
       |                  CSE 111 EXAM BUG TAXONOMY                |
       +-----------------------------+-----------------------------+
                                     |
         +---------------------------+---------------------------+
         |                           |                           |
         v                           v                           v
  [COMPILE-TIME ERRORS]      [RUNTIME EXCEPTIONS]      [SILENT LOGIC TRAPS]
  * Accidental return type   * NullPointerException    * Parameter self-assignment
  * Vanishing default constr * StackOverflowError      * Reference reassignment
  * Overload by return type  * OutOfBounds on arrays   * == instead of .equals()
  * this() not on line 1                               * Encapsulation ref leaks
```

---

### Group A: Constructor Pitfalls

#### Trap A1: Accidental Return Type (Turned Into Normal Method)
```java
class Student {
    // ❌ BUG: Adding 'void' turns this into an ordinary instance method, NOT a constructor!
    public void Student(String name) {
        System.out.println("Student: " + name);
    }
}

// In main():
Student s = new Student("Bob"); 
// ❌ COMPILE ERROR: constructor Student(java.lang.String) is undefined
```
* **Why it fails**: A constructor **must never** have a return type. `public void Student()` compiles as a standard instance method that happens to have the same name as the class.
* **How to fix**: Delete `void` completely: `public Student(String name) { ... }`.

---

#### Trap A2: The "Vanishing" Default Constructor
```java
class Book {
    String title;
    
    // Explicit parameterized constructor declared:
    public Book(String title) {
        this.title = title;
    }
}

// In main():
Book b = new Book(); 
// ❌ COMPILE ERROR: constructor Book() is undefined
```
* **Why it fails**: Java only provides an automatic no-arg constructor if the class has **zero** constructors declared. The moment you write `Book(String)`, the default no-arg constructor vanishes.
* **How to fix**: If you want both, explicitly write the no-arg constructor:
```java
public Book() {} // Explicit no-arg constructor
```

---

#### Trap A3: Calling `this(...)` on Any Line Other Than Line 1
```java
class Coordinate {
    int x, y;
    
    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public Coordinate() {
        System.out.println("Creating default coordinate..."); // ❌ Statement before this()
        this(0, 0); 
        // ❌ COMPILE ERROR: Constructor call must be the first statement in a constructor
    }
}
```
* **Why it fails**: Java enforces that constructor delegation runs before any user logic in the constructor body.
* **How to fix**: `this(0, 0);` must be the absolute first statement inside the constructor.

---

#### Trap A4: Recursive / Cyclic Constructor Chaining
```java
class Widget {
    int size;

    public Widget() {
        this(10); // Calls 1-arg constructor
    }

    public Widget(int size) {
        this(); // ❌ Calls 0-arg constructor!
        // ❌ COMPILE ERROR: Recursive constructor invocation
    }
}
```
* **Why it fails**: Java's compiler detects cycles in constructor delegation (`A -> B -> A`) and terminates compilation immediately.

---

#### Trap A5: Referencing Instance Variables Inside `this(...)`
```java
class Box {
    int defaultDepth = 5;

    public Box(int w, int h, int d) { ... }

    public Box(int w, int h) {
        // ❌ COMPILE ERROR: cannot reference this before supertype constructor has been called
        this(w, h, defaultDepth); 
    }
}
```
* **Why it fails**: Instance variables do not exist until constructor delegation has completed. You cannot use instance state as arguments to `this(...)`.
* **How to fix**: Pass a literal, static value, or constant: `this(w, h, 5);`.

---

#### Trap A6: Self-Instantiating Instance Initializer (`StackOverflowError`)
```java
class Node {
    int val;
    // ❌ RUNTIME CRASH: Every Node instantiation allocates another Node, ad infinitum!
    Node next = new Node(); 
}

// In main():
Node n = new Node(); 
// ❌ RUNTIME EXCEPTION: java.lang.StackOverflowError
```
* **Why it fails**: Initializer expressions run whenever `new Node()` is called, causing infinite recursive allocation until the JVM stack runs out of memory.
* **How to fix**: Initialize `Node next = null;` and assign it later.

---

### Group B: Overloading & Method Signature Traps

#### Trap B1: Attempting to Overload by Return Type Alone
```java
class Calculator {
    public int compute(int a, int b) { return a + b; }

    // ❌ COMPILE ERROR: method compute(int,int) is already defined in class Calculator
    public double compute(int a, int b) { return (double)(a + b); }
}
```
* **Why it fails**: Method signatures comprise **name + parameter type list**. Return types are ignored during overload resolution because callers can execute `compute(5, 10);` without capturing the return value, making it ambiguous.

---

#### Trap B2: Ambiguous Method Call with Primitive Promotion / Widening
```java
class AmbiguityDemo {
    public static void display(int a, double b) {
        System.out.println("int, double");
    }

    public static void display(double a, int b) {
        System.out.println("double, int");
    }

    public static void main(String[] args) {
        // ❌ COMPILE ERROR: reference to display is ambiguous
        // Both display(int, double) and display(double, int) match by widening int 5 -> double 5.0
        display(5, 5); 
        
        // How to fix: Explicitly cast one parameter to resolve ambiguity:
        display(5, (double)5); // Calls display(int, double)
    }
}
```

---

#### Trap B3: Ambiguous Overloaded Call with `null`
```java
class NullAmbiguity {
    public static void show(String s) { System.out.println("String"); }
    public static void show(Integer i) { System.out.println("Integer"); }

    public static void main(String[] args) {
        // ❌ COMPILE ERROR: reference to show is ambiguous
        // 'null' can legally be cast to both String and Integer!
        show(null);

        // How to fix: Explicit cast to clarify intention:
        show((String) null); // Prints: String
    }
}
```

---

### Group C: Scope, Shadowing & Variable Traps

#### Trap C1: Parameter Shadowing Bug (Self-Assignment)
```java
class Circle {
    double radius; // Default is 0.0

    public void setRadius(double radius) {
        // ❌ SILENT LOGIC BUG: Assigns local parameter variable to itself!
        // Instance variable 'this.radius' remains 0.0!
        radius = radius; 
    }
}

// In main():
Circle c = new Circle();
c.setRadius(7.5);
System.out.println(c.radius); // Prints: 0.0 (NOT 7.5!)
```
* **How to fix**: Always use `this.radius = radius;`.

---

#### Trap C2: Re-Declaring Instance Variable Inside Method (Type Shadowing)
```java
class Car {
    String model;

    public Car(String m) {
        // ❌ SILENT BUG: Declares a NEW local variable named 'model' inside the constructor!
        // The instance variable remains null!
        String model = m; 
    }
}

// In main():
Car myCar = new Car("Tesla");
System.out.println(myCar.model); // Prints: null!
```
* **How to fix**: Remove the type prefix: `this.model = m;`.

---

### Group D: References, Aliasing & Memory Traps

#### Trap D1: Reassigning an Object Reference Inside a Method (Expecting Caller to Change)
```java
class Wrapper {
    int val;
    Wrapper(int v) { this.val = v; }
}

public class SwapTest {
    public static void badSwap(Wrapper a, Wrapper b) {
        // ❌ Swaps only local stack copies of addresses!
        Wrapper temp = a;
        a = b;
        b = temp;
    }

    public static void main(String[] args) {
        Wrapper w1 = new Wrapper(10);
        Wrapper w2 = new Wrapper(20);
        badSwap(w1, w2);
        System.out.println(w1.val + ", " + w2.val); 
        // Prints: 10, 20 (Caller was completely unaffected!)
    }
}
```
* **Why it fails**: Java passes reference variables by value (copy of the address). Reassigning `a` or `b` only changes what the local variable points to during method execution.
* **How to fix**: Mutate object state instead:
```java
public static void goodSwap(Wrapper a, Wrapper b) {
    int temp = a.val;
    a.val = b.val;
    b.val = temp;
}
```

---

#### Trap D2: Array of Objects `NullPointerException`
```java
class Player {
    String name;
    void play() { System.out.println(name + " plays"); }
}

public class Game {
    public static void main(String[] args) {
        Player[] roster = new Player[5]; // Allocates array of 5 null pointers!
        
        // ❌ RUNTIME EXCEPTION: java.lang.NullPointerException
        roster[0].play(); 
    }
}
```
* **Why it fails**: `new Player[5]` only creates an array holding 5 `null` references; it **does not** instantiate any `Player` objects!
* **How to fix**: Instantiate each slot individually:
```java
roster[0] = new Player();
roster[0].play(); // Works!
```

---

#### Trap D3: Object Comparison with `==` vs `.equals()`
```java
class Student {
    int id;
    Student(int id) { this.id = id; }
}

public class Test {
    public static void main(String[] args) {
        Student s1 = new Student(101);
        Student s2 = new Student(101);

        // ❌ Checks HEAP ADDRESS IDENTITY, not content!
        System.out.println(s1 == s2); // Prints: false!

        Student s3 = s1; // Aliasing
        System.out.println(s1 == s3); // Prints: true (Same heap address)
    }
}
```
* **Rule**: `==` on objects compares memory addresses.

---

#### Trap D4: Encapsulation Breach via Mutable Object Reference Leak
```java
class Team {
    private int[] scores; // Private array

    public Team(int[] scores) {
        // ❌ BUG: Directly stores external array reference!
        this.scores = scores; 
    }

    public int[] getScores() {
        // ❌ BUG: Returns direct internal reference!
        return this.scores; 
    }
}

// In main():
int[] original = {10, 20, 30};
Team t = new Team(original);

original[0] = 999; // ❌ Bypassed encapsulation! Internal team score changed!

t.getScores()[1] = 888; // ❌ Bypassed encapsulation again!
```
* **Why it fails**: Exposing references to mutable internal structures allows outside callers to mutate `private` state directly without setter validation.
* **How to fix**: Perform **defensive copying**:
```java
public Team(int[] scores) {
    this.scores = scores.clone(); // Clones array
}
public int[] getScores() {
    return this.scores.clone();   // Returns copy
}
```

---

### Group E: Static vs. Instance & Access Traps

#### Trap E1: Accessing Instance Members Directly from Static `main()`
```java
public class Demo {
    int instanceVar = 42;
    void instanceMethod() { System.out.println("Hello"); }

    public static void main(String[] args) {
        // ❌ COMPILE ERROR: non-static variable instanceVar cannot be referenced from a static context
        System.out.println(instanceVar); 

        // ❌ COMPILE ERROR: non-static method instanceMethod() cannot be referenced from a static context
        instanceMethod(); 

        // How to fix: Instantiate an object first!
        Demo d = new Demo();
        System.out.println(d.instanceVar); // Valid
        d.instanceMethod();                // Valid
    }
}
```

---

#### Trap E2: Accessing `private` Members Directly Across Classes
```java
class Safe {
    private int passcode = 1234;
}

public class Intruder {
    public static void main(String[] args) {
        Safe s = new Safe();
        // ❌ COMPILE ERROR: passcode has private access in Safe
        System.out.println(s.passcode); 

        // How to fix: Access via public getter: s.getPasscode();
    }
}
```

---

#### Trap E3: Printing or Assigning a Method that Returns `void`
```java
class Counter {
    void increment() { ... }
}

public class Test {
    public static void main(String[] args) {
        Counter c = new Counter();
        // ❌ COMPILE ERROR: 'void' type not allowed here
        System.out.println(c.increment()); 
    }
}
```

---

## 9. Exam Output-Trace Survival Rules

When tracing Java code in CSE 111 exam questions:

1. **Draw Stack and Heap boxes on scratch paper**:
   * Draw every local variable and parameter in a stack frame.
   * Draw every `new` object in the Heap with its memory address (e.g., `0x1`, `0x2`).
2. **Follow the Pointers**:
   * If code does `obj.field = val` or `obj.setField(val)`, follow the pointer arrow to the Heap and update the box.
   * If code does `obj = new Class(...)`, erase the arrow and draw a new arrow to a brand-new box on the Heap. The old box stays unchanged!
3. **Verify Constructor Defaults**:
   * Remember that before constructor lines execute, instance fields **already** hold their default values: numbers are `0`/`0.0`, booleans are `false`, object references are `null`.
4. **Watch for Shadowing**:
   * Check if a parameter has the exact same name as an instance variable. If there's no `this.`, the instance variable was **never updated**.
5. **Check for Aliasing**:
   * If you see `A a2 = a1;`, remember they are looking at the **same object**. Any change through `a2` will be seen when inspecting `a1`.
