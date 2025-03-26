![web_prak](https://github.com/user-attachments/assets/63c0a517-e766-4d2b-8ad7-1b0807cc03fc)### Головаш Денис 327

### Система учебного расписания ВУЗа

### **Концептуальная модель пользовательского интерфейса** {#концептуальная-модель-пользовательского-интерфейса .list-paragraph}

**Основные сущности:**

-   Студенты

-   Преподаватели

-   Аудитории

-   Курсы

-   Занятия

**Основные операции:**

-   Просмотр, добавление, редактирование, удаление данных.

-   Составление расписания.

-   Поиск свободных аудиторий.

-   Фильтрация списков (студенты по группам, преподаватели по курсам).

**Основные страницы:**

-   Главная

-   Список студентов

-   Список преподавателей

-   Список курсов

-   Список аудиторий

-   Расписание

-   Профиль студента

-   Профиль преподавателя

-   Профиль курса

**Схема навигации между страницами**

![web_prak](https://github.com/user-attachments/assets/f38ed21b-ce02-43b6-ab63-1c27d03bf1db)


### **Сценарии использования**

1.  **Просмотр списка студентов по группе/потоку:**

    -   Пользователь переходит на страницу \"Студенты\".

    -   Применяет фильтры: поток, группа.

    -   Видит отфильтрованный список студентов.

2.  **Просмотр списка преподавателей по курсам:**

    -   Пользователь переходит на страницу \"Преподователи\".

    -   Применяет фильтры: курс.

    -   Видит отфильтрованный список преподователей.

3.  **Поиск свободных аудиторий:**

    -   Пользователь переходит на страницу \"Аудитории\".

    -   Применяет фильтры: временной интервал.

    -   Получает список свободных аудиторий.

4.  **Просмотр расписания студента/преподавателя/аудитории:**

    -   Пользователь выбирает студента/преподавателя/аудиторю в списке.

    -   Переходит на страницу \"Расписание\" и применяется фильтр по
        > нужному студенту/преподавателю/аудитории.

5.  **Составление расписания на семестр:**

    -   Пользователь выбирает курс в списке курсов.

    -   Нажимает \"Составить расписание\".

    -   Далее есть поля для выбора преподавателя (фио),
        > аудиторию(номер), время, также есть кнопки для выбора охвата
        > если выбирается группу, появляется поле для ввода номера
        > группы/списка групп, если - поток, появляется поле для ввода
        > потока/списка потоков, если - спецкурс, появляется поле для
        > ввода максимального количества записей.

    -   При ввводе данных, для которых есть предположительные варианты
        > (преподаватель, аудитория, группа, поток) показывается список
        > вариантов подходящих по префиксу.

    -   При выборе таких вариантов поле очищается, показывается выбор с
        > кнопкой его отмены, и возможность ввести еще значение в данное
        > очищенное поле.

    -   Также происходят проверки (данный преподователь свободен в это
        > время, аудитория расчитана на данное количество человек, не
        > пересекается ли занятие с другими).

    -   Проверки не запрещают добавить такие данные в расписании
        > (сделано для спец. случаев), но они будут выдовать
        > предупреждение.

    -   Создавать расписание может только привелегированный пользователь
        > (учитель).

6.  **Чтение/редактирование/удаление данных
    > студента/преподователя/курса:**

    -   Пользователь выбирает студента/преподователя/курс в списке.

    -   Переходит на страницу \"Профиль студента\"/\"Профиль
        > преподователя\"/\"Профиль курса\".

    -   Нажимает \"Редактировать\"/\"Удалить\" и вносит изменения.

7.  **Добавление нового студента/преподователя/курса:**

    -   Пользователь переходит на страницу
        > \"Студенты\"/\"Преподователи\"/\"Курсы\".

    -   Нажимает \"Добавить студента\"/\"Добавить
        > преподователя\"/\"Добавить курс\".

    -   Заполняет форму.

8.  **Занесение студента в спецкурс:**

    -   Пользователь выбирает студента в списке.

    -   На странице профиля нажимает \"Добавить в спецкурс\".

    -   Выбирает курс из списка.

### **Описание страниц**

**Главная страница:**

-   Доступ к разделам: \"Студенты\", \"Преподаватели\", \"Аудитории\",
    \"Курсы\".

```{=html}
<!-- -->
```
-   Быстрый переход к нужному разделу.

**Страница \"Студенты\":**

-   Фильтрация списка студентов по потоку и группе.

-   Поиск студента по имени.

-   Переход в профиль студента.

-   Добавление нового студента.

-   Просмотр расписания студента.

**Страница \"Преподаватели\":**

-   Фильтрация списка преподавателей по курсу.

```{=html}
<!-- -->
```
-   Поиск преподавателя по имени.

```{=html}
<!-- -->
```
-   Переход в профиль преподавателя.

```{=html}
<!-- -->
```
-   Добавление нового студента.

```{=html}
<!-- -->
```
-   Просмотр расписания преподавателя.

**Страница \"Аудитории\":**

-   Фильтрация аудиторий по временному интервалу (Пользователь вводит
    > дату и время для начала поиски и для конца по типу 12.02.24 12:00
    > -- 12.02.24 15:30).

```{=html}
<!-- -->
```
-   Отображение списка доступных аудиторий (показывается список
    > аудиторий, в которых нет запланированных занятий в данный
    > диапозон - то есть номер и вместимость).

```{=html}
<!-- -->
```
-   Переход к расписанию аудитории.

**Страница \"Расписание\":**

-   Расписания студента, преподавателя или аудитории на неделю с
    > названием курсов и времени по дням.

```{=html}
<!-- -->
```
-   Создание расписания на семестр (выбор курса, аудитории, времени,
    > преподавателя).

**Страница \"Курсы\":**

-   Просмотр списка курсов (название, поток/группа/спецкурс,
    > интенсивность).

```{=html}
<!-- -->
```
-   Фильтрация курсов по параметрам.

```{=html}
<!-- -->
```
-   Переход в профиль курса.

```{=html}
<!-- -->
```
-   Добавление нового курса.

**Страница \"Профиль\":**

-   Просмотр детальной информации.

```{=html}
<!-- -->
```
-   Редактирование и удаление данных.

```{=html}
<!-- -->
```
-   Добавление студента в спецкурс.

### **Схема базы данных:**
![web_prak](https://github.com/user-attachments/assets/10852a11-8bb9-4a5c-b90e-be045475ba13)
