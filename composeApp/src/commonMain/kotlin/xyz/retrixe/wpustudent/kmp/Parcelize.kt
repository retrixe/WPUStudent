package xyz.retrixe.wpustudent.kmp

/*
I've only found one idiomatic replacement for using kotlinx-serialization instead of Parcelise:

Relevant links:
- https://developer.android.com/topic/libraries/architecture/viewmodel/viewmodel-savedstate
- https://developer.android.com/develop/ui/compose/state-lifespans
- https://developer.android.com/jetpack/androidx/releases/savedstate#1.3.0
- https://developer.android.com/jetpack/androidx/releases/lifecycle#2.9.0-alpha07

var savedData by savedStateHandle.saved<Data>("data") { Data.Loading }
// OR
var savedData by savedStateHandle.saved(Data.serializer(), "data") { Data.Loading }
// OR
inline fun <reified T> serializableSaver() = Saver<T, String>(
    save = { Json.encodeToString(it) }, // use encode/decodeToSavedState
    restore = { Json.decodeFromString(it) }
)
@OptIn(SavedStateHandleSaveableApi::class)
var savedData by savedStateHandle.saveable(key = "data", stateSaver = serializableSaver<Data>()) { mutableStateOf(Data.Loading) }
// AND
private val _data = MutableStateFlow(savedData)
val data = _data.asStateFlow()

// then when updating data
savedData = Data.Loaded(summary) // or whatever value you want to put in
_data.value = Data.Loaded(summary)
*/

// There is this library too: https://github.com/chRyNaN/serialization-parcelable

// https://stackoverflow.com/questions/70916976/how-to-use-parcelize-annotation-in-shared-module-of-kotlin-multiplatform-projec/78543072#78543072
// http://developer.android.com/kotlin/parcelize#setup_parcelize_for_kotlin_multiplatform
// FINALLY THANK YOU FOR SOME USEFUL DOCS THAT GOOGLE WOULDNT SURFACE FFS

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class Parcelize

expect interface Parcelable

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
expect annotation class IgnoredOnParcel
