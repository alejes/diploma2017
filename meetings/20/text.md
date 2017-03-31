### Дизайн

Стоит ли давать пользователю возможность делать callback на отсутствующие методы и поля?
Это к вопросу о выражении ExpandoObject.

```
dynamic employee = new ExpandoObject();
employee.Name = "John Smith";
employee.Age = 33;

foreach (var property in (IDictionary<String, Object>)employee)
{
    Console.WriteLine(property.Key + ": " + property.Value);
}
// This code example produces the following output:
// Name: John Smith
// Age: 33
```
