# 📅 College Schedule App

## 📱 Описание проекта

**College Schedule App** — это мобильное приложение на платформе **Android**, предназначенное для студентов колледжа. Приложение предоставляет удобный интерфейс для просмотра расписания занятий с возможностью фильтрации по дате и группе. Также реализованы функции регистрации, авторизации и личного кабинета студента.

---

## 🔧 Основной функционал

1. **Регистрация пользователя**
   - Ввод имени, адреса электронной почты, пароля, повтора пароля, номера телефона.
   - Проверка корректности данных.
   - Сохранение данных в базе данных (Supabase).

2. **Авторизация пользователя**
   - Ввод электронной почты и пароля.
   - Аутентификация через Supabase.

3. **Личный кабинет**
   - Отображение персональной информации: аватар, имя, номер телефона.
   - Возможность редактирования данных.

4. **Окно с расписанием**
   - Отображение расписания занятий.
   - Фильтрация по группе и дате.

---

## ⚙️ Технологии

- **Язык программирования:** Kotlin
- **Среда разработки:** Android Studio
- **Бэкенд:** Supabase (аутентификация, база данных)
- **Тестирование:** Unit-тесты и UI-тесты
- **Архитектура:** MVVM
- **Диаграммы:** UML (классов, активности, последовательностей и др.)

---

## 👨‍💻 Команда и роли

### 📌 Разработчик Android / Дизайнер (UI/UX) – Казаков Илья
**Обязанности:**
- Разработка интерфейса приложения (UI/UX-дизайн)
- Реализация части функционала (в связке с бэкенд-разработчиком)
- Создание и поддержка:
  - Диаграммы классов
  - Диаграммы деятельности
  - Диаграммы последовательности

**Отвечает за модули:**
- Реализация авторизации

---

### 📌 Разработчик Android / Тестировщик – Жамалетдинов Руслан
**Обязанности:**
- Реализация основного функционала (личный кабинет)
- Написание модульных и UI-тестов
- Тестирование:
  - Авторизации
  - Регистрации
  - Личного кабинета
- Создание:
  - Диаграммы кооперации
  - Диаграммы развертывания

**Отвечает за модули:**
- Реализация личного кабинета

---

### 📌 Бэкенд-разработчик / Android-разработчик / Тестировщик – Голубев Никита
**Обязанности:**
- Интеграция с Supabase (аутентификация, база данных)
- Полная реализация функционала:
  - Регистрации
- Тестирование личного кабинета
- Создание:
  - Диаграммы состояний
  - Диаграммы вариантов использования
  - Диаграммы компонентов

**Отвечает за модули:**
- Реализация регистрации

---

## 📌 Планы на будущее

- Добавление напоминаний о занятиях.
- Внедрение уведомлений о замене пар.
- Возможность добавления личных заметок.
- Интеграция с календарем Google.

---

