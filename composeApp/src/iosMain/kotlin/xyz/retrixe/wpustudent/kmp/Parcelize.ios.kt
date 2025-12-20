package xyz.retrixe.wpustudent.kmp

actual interface Parcelable

@Target(allowedTargets = [AnnotationTarget.PROPERTY])
@Retention(value = AnnotationRetention.SOURCE)
actual annotation class IgnoredOnParcel
