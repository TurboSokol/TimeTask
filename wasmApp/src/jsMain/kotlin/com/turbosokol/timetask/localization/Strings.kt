/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.TimeTask.localization

/**
 * Localization strings for the TimeTask application
 * Supports English and Russian languages
 */
object Strings {
    
    // App name
    const val app_name = "TimeTask"
    
    // Main screen
    const val task_manager = "Task Manager"
    const val timetask_wasm = "TimeTask - WASM"
    const val loading_tasks = "Loading tasks..."
    const val error_prefix = "Error: %s"
    const val retry = "Retry"
    const val no_tasks_yet = "No tasks yet"
    const val no_tasks_yet_message = "Tap the + button to create your first task"
    const val no_tasks_yet_wasm = "No tasks yet. Add one to get started!"
    
    // Task creation
    const val create_new_task = "Create New Task"
    const val add_new_task = "Add New Task"
    const val task_title = "Task Title"
    const val task_description = "Task Description"
    const val enter_task_title = "Enter task title"
    const val enter_task_description = "Enter task description"
    const val choose_color = "Choose Color"
    const val create_task = "Create Task"
    const val add = "Add"
    
    // Task editing
    const val edit_task = "Edit Task"
    const val time_settings = "Time Settings"
    const val seconds = "Seconds"
    const val hours = "Hours"
    const val update = "Update"
    
    // Common actions
    const val cancel = "Cancel"
    const val delete = "Delete"
    const val start = "Start"
    const val pause = "Pause"
    const val reset_time = "Reset time"
    
    // Content descriptions
    const val add_task = "Add Task"
    const val background = "Background"
    const val add_task_content_desc = "Add task"
    
    // Time display
    const val hours_display = "%.1f hours"
    
    // Form fields
    const val title = "Title"
    const val description_optional = "Description (optional)"
    
    // Placeholders
    const val placeholder_zero = "0"
    const val placeholder_zero_decimal = "0.0"
}

/**
 * Russian localization strings
 */
object StringsRu {
    
    // App name
    const val app_name = "TimeTask"
    
    // Main screen
    const val task_manager = "Менеджер задач"
    const val timetask_wasm = "TimeTask - WASM"
    const val loading_tasks = "Загрузка задач..."
    const val error_prefix = "Ошибка: %s"
    const val retry = "Повторить"
    const val no_tasks_yet = "Пока нет задач"
    const val no_tasks_yet_message = "Нажмите кнопку +, чтобы создать первую задачу"
    const val no_tasks_yet_wasm = "Пока нет задач. Добавьте одну, чтобы начать!"
    
    // Task creation
    const val create_new_task = "Создать новую задачу"
    const val add_new_task = "Добавить новую задачу"
    const val task_title = "Название задачи"
    const val task_description = "Описание задачи"
    const val enter_task_title = "Введите название задачи"
    const val enter_task_description = "Введите описание задачи"
    const val choose_color = "Выберите цвет"
    const val create_task = "Создать задачу"
    const val add = "Добавить"
    
    // Task editing
    const val edit_task = "Редактировать задачу"
    const val time_settings = "Настройки времени"
    const val seconds = "Секунды"
    const val hours = "Часы"
    const val update = "Обновить"
    
    // Common actions
    const val cancel = "Отмена"
    const val delete = "Удалить"
    const val start = "Старт"
    const val pause = "Пауза"
    const val reset_time = "Сбросить время"
    
    // Content descriptions
    const val add_task = "Добавить задачу"
    const val background = "Фон"
    const val add_task_content_desc = "Добавить задачу"
    
    // Time display
    const val hours_display = "%.1f часов"
    
    // Form fields
    const val title = "Название"
    const val description_optional = "Описание (необязательно)"
    
    // Placeholders
    const val placeholder_zero = "0"
    const val placeholder_zero_decimal = "0.0"
}

/**
 * Language enum for localization
 */
enum class Language {
    ENGLISH, RUSSIAN
}

/**
 * Localization manager to get strings based on current language
 */
object LocalizationManager {
    private var currentLanguage: Language = Language.ENGLISH
    
    fun setLanguage(language: Language) {
        currentLanguage = language
    }
    
    fun getCurrentLanguage(): Language = currentLanguage
    
    fun getString(stringKey: String): String {
        return when (currentLanguage) {
            Language.ENGLISH -> getEnglishString(stringKey)
            Language.RUSSIAN -> getRussianString(stringKey)
        }
    }
    
    private fun getEnglishString(key: String): String {
        return when (key) {
            "app_name" -> Strings.app_name
            "task_manager" -> Strings.task_manager
            "timetask_wasm" -> Strings.timetask_wasm
            "loading_tasks" -> Strings.loading_tasks
            "error_prefix" -> Strings.error_prefix
            "retry" -> Strings.retry
            "no_tasks_yet" -> Strings.no_tasks_yet
            "no_tasks_yet_message" -> Strings.no_tasks_yet_message
            "no_tasks_yet_wasm" -> Strings.no_tasks_yet_wasm
            "create_new_task" -> Strings.create_new_task
            "add_new_task" -> Strings.add_new_task
            "task_title" -> Strings.task_title
            "task_description" -> Strings.task_description
            "enter_task_title" -> Strings.enter_task_title
            "enter_task_description" -> Strings.enter_task_description
            "choose_color" -> Strings.choose_color
            "create_task" -> Strings.create_task
            "add" -> Strings.add
            "edit_task" -> Strings.edit_task
            "time_settings" -> Strings.time_settings
            "seconds" -> Strings.seconds
            "hours" -> Strings.hours
            "update" -> Strings.update
            "cancel" -> Strings.cancel
            "delete" -> Strings.delete
            "start" -> Strings.start
            "pause" -> Strings.pause
            "reset_time" -> Strings.reset_time
            "add_task" -> Strings.add_task
            "background" -> Strings.background
            "add_task_content_desc" -> Strings.add_task_content_desc
            "hours_display" -> Strings.hours_display
            "title" -> Strings.title
            "description_optional" -> Strings.description_optional
            "placeholder_zero" -> Strings.placeholder_zero
            "placeholder_zero_decimal" -> Strings.placeholder_zero_decimal
            else -> key
        }
    }
    
    private fun getRussianString(key: String): String {
        return when (key) {
            "app_name" -> StringsRu.app_name
            "task_manager" -> StringsRu.task_manager
            "timetask_wasm" -> StringsRu.timetask_wasm
            "loading_tasks" -> StringsRu.loading_tasks
            "error_prefix" -> StringsRu.error_prefix
            "retry" -> StringsRu.retry
            "no_tasks_yet" -> StringsRu.no_tasks_yet
            "no_tasks_yet_message" -> StringsRu.no_tasks_yet_message
            "no_tasks_yet_wasm" -> StringsRu.no_tasks_yet_wasm
            "create_new_task" -> StringsRu.create_new_task
            "add_new_task" -> StringsRu.add_new_task
            "task_title" -> StringsRu.task_title
            "task_description" -> StringsRu.task_description
            "enter_task_title" -> StringsRu.enter_task_title
            "enter_task_description" -> StringsRu.enter_task_description
            "choose_color" -> StringsRu.choose_color
            "create_task" -> StringsRu.create_task
            "add" -> StringsRu.add
            "edit_task" -> StringsRu.edit_task
            "time_settings" -> StringsRu.time_settings
            "seconds" -> StringsRu.seconds
            "hours" -> StringsRu.hours
            "update" -> StringsRu.update
            "cancel" -> StringsRu.cancel
            "delete" -> StringsRu.delete
            "start" -> StringsRu.start
            "pause" -> StringsRu.pause
            "reset_time" -> StringsRu.reset_time
            "add_task" -> StringsRu.add_task
            "background" -> StringsRu.background
            "add_task_content_desc" -> StringsRu.add_task_content_desc
            "hours_display" -> StringsRu.hours_display
            "title" -> StringsRu.title
            "description_optional" -> StringsRu.description_optional
            "placeholder_zero" -> StringsRu.placeholder_zero
            "placeholder_zero_decimal" -> StringsRu.placeholder_zero_decimal
            else -> key
        }
    }
}
