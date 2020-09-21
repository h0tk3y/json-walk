# json-walk #

Задача – реализовать консольный инструмент для просмотра и редактирования JSON-файлов.

## Запуск ##

Программа запускается либо с флагом `--new`, и тогда работа начинается с пустого JSON)
либо с `--file filename.json` – тогда JSON считывается из указанного файла.

После запуска программа считывает команды по одной строке из стандартного ввода и отвечает на каждую команду.

## Команды ##

* `path` – напечатать текущий путь от корня. 
  Пример пути: `foo/bar/[0]/[1]`, индексы массивов – в квадратных скобках.

    Пример:
    ```
    > path
    OK
    > go a/b/c
    OK
    >path
    OK a/b/c
    >up
    OK
    >path
    a/b
    ```
  
* `print` – напечатать поддерево узла, в котором мы стоим

    Пример:
    ```
    print
    OK {"a":{"b":123}}
    ```
    
* `go <path>` - спуститься в дочерний узел `path`. Например, `go foo`, `go foo/bar/baz`, `go foo/[0]/[0]`
  
* `up` - подняться к родительскому элементу, на уровень выше.
  
* `insert <path> <newElement>` - вставить/добавить элемент в дочерний узел по данному пути. 
  
   Примеры использования: 
   ```
   insert foo/[0] {}
   insert foo/[0]/a "x"
   insert foo/x/y/z/a/b/c true
   ```
  
     Для добавления элемента в конец массива используется `[_]`. Если последний сегмент пути – индекс в массиве, то
   вставить новый элемент нужно по указанному индексу, сдвинув остальные элементы.
  
* `replace <path> <newElement>` - заменить элемент в дочернем узле. 
   Примеры использования:
   ```
   replace foo/[0] { "bar": "baz }
   replace foo/[0]/bar 123
   ```

* `delete <path>` - удалить узел.
  
* `copy <path>` - пометить узел для последующего копирования с помощью `paste`.
  
* `paste` - скопировать помеченный ранее узел (поддерево) по тому же относительному пути в текущий элемент.
  
* `findpath <pattern>` -  поиск путей, которые совпадают с шаблоном. Например, 
  `find a/*/[0]` на объекте `{ "a": { "x": [123], "y": [123], "z": [] } }` должно было вывести:
    ```
    OK
    a/x/[0]
    a/y/[0]
    ```

## Дополнительные команды ##                          

* Поддержать операции с `*` в качестве сегмента пути. В модифицирующих операциях использование таких путей делает 
  операцию множественной (вставляется, заменяется, удаляется или копируется более одного элемента сразу). В операции
  `findpaths` сегмент `*` соответствует любому сегменту. 
* `load <filename>` - загрузить json из файла `<filename>`. В случае успешной загрузки он заменяет текущий. пользователь перемещается в корень.
* `save <filename>` - сохранить json в файл `<filename>`. Если он уже существует, спросить пользователя, нужно ли его переписать.

## Пример сессии ##
```
insert a { "b": "c", "array": [123] }
OK
print
OK {"a":{"b":"c","array":[123]}}
insert a/array/[_] []
OK
print
OK {"a":{"b":"c","array":[123,[]]}}
replace a/array/[0] []
OK
print
OK {"a":{"b":"c","array":[[],[]]}}
insert a/array/*/[0] 123
OK
print
OK {"a":{"b":"c","array":[[123],[123]]}}
insert a/x "y"
OK
print
OK {"a":{"b":"c","array":[[123],[123]],"x":"y"}}
replace a/b []
OK
replace a/y []
bad-path 1
replace a/x []
OK
print
OK {"a":{"b":[],"array":[[123],[123]],"x":[]}}
insert a/*/[_] 456
OK
print
OK {"a":{"b":[456],"array":[[123],[123],456],"x":[456]}}
```