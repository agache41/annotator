# Annotator

Annotation services over Java Beans

** Annotator ** is a plain Java library targeted on delivering
an uniform reflection and annotation API over
multiple [ORM](https://en.wikipedia.org/wiki/Object%E2%80%93relational_mapping) use cases.

The API is defined in the main Interface [Annotate](src/main/java/io/github/agache41/annotator/annotator/Annotate.java).
Following methods are available :

- get()                           - Gets the underlining "annotated" scope (class, field, method ..etc)
- hasAnnotation(Class clazz)      - Tells if in the current scope contains annotation of the given type.
- getAnnotation(Class annotationClass ...) - Gets the annotation present in scope of the given type parameter.
- getAnnotations()                - Gets the annotations present in scope.
- getAnnotations(Class clazz)  - Gets the annotations from the current scope of the given type.
- getAnnotationsThat(Matcher matcher)- Gets the annotations from the current scope that match the given matcher.
- getField(String name)           - Returns the field with the given name present in scope.
- getFields()                     - Returns the fields present in scope.
- getFieldsThat(Matcher matcher)  - Gets the fields from the current scope that match the given matcher.
- getMethods()                    - Returns the methods present in scope.
- getMethodsThat(Matcher matcher) - Gets the methods from the current scope that match the given matcher.
- getAccessor(String name)        - Returns the accessor with the given name present in scope.
- getAccessors()                  - Returns the accessors present in scope.
- getAccessorsThat(Matcher matcher) - Gets the accessors from the current scope that match the given matcher.

Using the annotator class this API can be queried on multiple points of interest from the ORM perspective :
classes,fields, methods, objects.
The Library also introduces the concept of accessor, another unified API over properties of a class.
The Accessor class provides uniform access methods over values stored in a Java Bean, either using setter-getter or
direct field access.
It also provides uniform access over the annotations present on the field or methods that compose this pattern.

- [Quick start](#quickstart)
    - [BaseTestClass](#baseTestClass)
    - [TestExtends](#testExtends)
    - [MarkedClass](#markedclass)
    - [Querying](#querying)
    - [Filtering](#filtering)
    - [Fields](#fields)
    - [Limitations](#limitations)
    - [Testing](#testing)
- [Demo](#demo)
- [Requirements](#requirements)
- [Installation](#installation)
- [Features](#features)
- [Structure](#structure)

## Quickstart

Let's start by creating a set of annotations and a class where we plan on using them.
The class where these annotation will be used will be named scope.
Then we will use the Annotator class to query this structure and read it.

### BaseTestClass

The annotation BaseTestClass acts as a marker interface, defining the base class used in annotation extending.
Even though inheritance does not really work for annotations, this use case is more of a marker point in identifying
specific annotations that contribute to our pattern and offers a clean method of separating them from other annotations
present in the current scope.

```java

@Retention(RetentionPolicy.RUNTIME)
public @interface BaseTestClass {
}
```

### TestExtends

The annotations @TestExtends and @TestExtendsWithValue are our target annotations.
Notice that they are annotated with @Extends, providing our marker annotation BaseTestClass.class.

```java

@Extends(BaseTestClass.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface TestExtends {
}


@Extends(BaseTestClass.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface TestExtendsWithValue {
    String value();
}

```

### MarkedClass

Now let's place the annotations on several points in the class:

```java

@Data
@TestExtends
@TestExtendsWithValue("myClassValue")
public class MarkedClass {

    @TestExtends
    private String annotatedField;

    private String notAnnotatedField;

    @TestExtendsWithValue("myValue")
    private String annotatedWithValue;

    @TestExtends
    @TestExtendsWithValue("myBothValue")
    private String annotatedWithBoth;
}
```

Notice the used @Data annotation from [Lombok](https://projectlombok.org/).

### Querying

The Annotator instance can be created on a class, instance of a class (object), field , method and accessor.
Let's create it on a new MarkedClass instance and query the present annotations.

```java
final List<Annotation> annotations = Annotator
        .of(new MarkedClass())
        .getAnnotations()
        .collect(Collectors.toList());
```

As expected the list will contain the @Data and the two @TestExtends and @TestExtendsWithValue annotations instances
present on the class.

### Filtering

But in our use case we are only interested in getting the two @TestExtends and @TestExtendsWithValue annotation.
To do this we have to filter the annotations stream.
A naive approach would be to filter by using the two class types.

```java
final List<TestExtends> annotations = Annotator
        .of(new MarkedClass())
        .getAnnotations(TestExtends.class)
        .collect(Collectors.toList());

final List<TestExtends> annotations = Annotator
        .of(new MarkedClass())
        .getAnnotations(TestExtendsWithValue.class)
        .collect(Collectors.toList());
```

This will, however, bring us two collections that have to be merged together
and the code would be difficult to maintain when a third annotation
in the use case comes in question (Example would be a hypothetical @TestExtendsWithTwoValues)

So let's focus the filtering on the common ground of the two annotation,
that is the fact that both are being annotated with @Extends.

The annotator class provides us with already built in methods for filtering
and the much-needed Matcher and Predicate instances.

Enjoy reading the code!

```java
final List<Annotation> annotations = Annotator
        .of(new MarkedClass())
        .getAnnotationsThat(HaveAnnotation.ofType(Extends.class)
                                          .having(AnExtendsValue.of(BaseTestClass.class)))
        .collect(Collectors.toList());
```

This will " get the annotations that have an annotation of type Extends, having an extends value of BaseTestClass ".
So the two @TestExtends and @TestExtendsWithValue will be returned.

### Fields

In a very similar way we can query the class for the Fields that are annotated with one of our annotations:

```java
final List<Field> fields = Annotator
        .of(new MarkedClass())
        .getFieldsThat(HaveAnnotation.ofType(TestExtends.class))
        .collect(Collectors.toList());
```

or both

```java
final List<Field> fields = Annotator
        .of(new MarkedClass())
        .getFieldsThat(HaveAnnotation.ofType(TestExtends.class)
                                     .and(HaveAnnotation.ofType(TestExtendsWithValue.class)))
        .collect(Collectors.toList());
```

The Fields obtained from the class can be further queried:

```java
Field field;
final List<Annotation> annotations = Annotator
        .of(field)
        .getAnnotationsThat(HaveAnnotation.ofType(Extends.class)
                                          .having(AnExtendsValue.of(BaseTestClass.class)))
        .collect(Collectors.toList());
```

The Annotator.of(class/object/field/method/accessor) method acts as a central singleton instance factory
and is the only method that has to be used for implementing queries.
The instances are being created at first call and are being reused and cached for optimization reasons.

### Limitations

Querying for annotations can be done on every instance, that is because class, fields or methods can have them.
Querying for fields on a class follows the same principle. You can not however look for like fields of methods
on a field or method, an IllegalStateException will be thrown.

### Testing

Once the annotations and the using class are created, testing them can be done by querying and comparing results:
Here is an example looking for fields marked with @TestExtends annotation

```java

@Test
public void testFieldsThatHaveTestExtends() {
    final List<Field> fields = Annotator
            .of(new MarkedClass())
            .getFieldsThat(HaveAnnotation
                                   .ofType(TestExtends.class))
            .collect(Collectors.toList());

    Assertions.assertEquals(2, fields.size());
    final Field field = fields.get(0);
    Assertions.assertNotNull(field);
    Assertions.assertEquals(String.class, field.getType());
}
```

### Data Access

### Resource Service again

### Testing

### Testing my own methods

## Demo

//:todo
А comprehensive example of using the library with JPA database you can find in the **[demo](/demo)** module.

## Requirements

The library works with Java 8+.

## Installation

Simply add  `io.github.agache41:annotator` dependency to your project.

```xml

<dependency>
    <groupId>io.github.agache41</groupId>
    <artifactId>annotator</artifactId>
    <version>version</version>
</dependency>
```

## Features

- Easy to install, just add it to the classpath.
- Easy to use, just compose your queries using the predicates.

## Structure

The library is packaged as a single jar.

## Dependencies

```xml

<dependencies>
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-lang3</artifactId>
        <version>3.12.0</version>
    </dependency>
</dependencies>
```

Testing dependencies are listed here. Please note the Lombok is used only in the test context.

```xml

<dependencies>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter-engine</artifactId>
        <version>5.9.0</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.18.24</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```
