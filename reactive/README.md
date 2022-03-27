# Essentials Java building blocks

Essentials is a set of Java version 11 (and later) building blocks built from the ground up to have no dependencies on other libraries, unless explicitly mentioned.

The Essentials philosophy is to provide high level building blocks and coding constructs that allows for concise and strongly typed code, which doesn't depend on other libraries or frameworks, but
instead allows easy integrations with many of the most popular libraries and frameworks such as Jackson, Spring Boot, Spring Data, JPA, etc.

## Reactive

This library contains the smallest set of supporting reactive building blocks needed for other Essentials libraries.

To use `Reactive` just add the following Maven dependency:
```
<dependency>
    <groupId>dk.cloudcreate.essentials</groupId>
    <artifactId>reactive</artifactId>
    <version>0.5.5</version>
</dependency>
```

### LocalEventBus
Simple event bus that supports both synchronous and asynchronous subscribers that are registered and listening for events published within the local the JVM  
You can have multiple instances of the LocalEventBus deployed with the local JVM, but usually one event bus is sufficient.

```
LocalEventBus<OrderEvent> localEventBus    = new LocalEventBus<>("TestBus", 3, (failingSubscriber, event, exception) -> log.error("...."));
                  
localEventBus.addAsyncSubscriber(orderEvent -> {
           ...
       });

localEventBus.addSyncSubscriber(orderEvent -> {
             ...
       });
                  
localEventBus.publish(new OrderCreatedEvent());
 ```