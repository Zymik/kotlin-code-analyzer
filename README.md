# Аналазитор кода
Дз по верификации ПО

Проверяется правило, что приватные `lateinit` поля верхнеуровненвых класса инциализируются где-то в коде класс, либо имеют специальную аннотацию, например, `@Autowired`.

Примеры валидных классов:
```kotlin
class LateInitAssignment {

    @Autowired
    private lateinit var s: Test

}
```

```kotlin
class LateInitAssignment {
    private lateinit var s: Test

    fun assignment() {
        s = Test()
    }
}
```

Пример невалидного:
```kotlin
class LateInitAssignment {
    private lateinit var s: Test

    fun assignment() {
        var s = 1
        s = 2
    }
}
```
