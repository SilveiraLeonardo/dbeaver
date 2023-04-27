
#include "StdAfx.h"
#include "JNIMetaData.h"
#include "WMIUtils.h"
#include <map>
#include <mutex>

// Mutex to protect access to the singleton instance
static std::mutex instance_mutex;

JNIMetaData* JNIMetaData::instance = NULL;

JNIMetaData::JNIMetaData(JNIEnv* pEnv) : pJavaEnv(pEnv)
{
    // Initialize Java classes and methods with proper error handling
    javaLangObjectClass = FindJavaClass("java/lang/Object");
    if (!javaLangObjectClass) {
        return; // Alternatively, throw an exception or terminate the program
    }
    // ... (Initialize all other classes and methods)

    // Check the return values of GetFieldID and GetMethodID calls
    wmiServiceHandleField = pJavaEnv->GetFieldID(wmiServiceClass, "serviceHandle", "J");
    if (!wmiServiceHandleField) {
        return; // Alternatively, throw an exception or terminate the program
    }
    // ... (Check other GetFieldID and GetMethodID calls)

}

// ...

JNIMetaData& JNIMetaData::GetMetaData(JNIEnv* pEnv)
{
    if (instance == NULL) {
        std::unique_lock<std::mutex> lock(instance_mutex);
        if (instance == NULL) {
            instance = new JNIMetaData(pEnv);
            if (!instance->javaLangObjectClass) {
                delete instance;
                instance = NULL;
                // Throw an exception, return an error code, or terminate the program
            }
        }
    }
    return *instance;
}

// ...

jclass JNIMetaData::FindJavaClass(const char* className)
{
    jclass clazz = pJavaEnv->FindClass(className);
    if (clazz == NULL) {
        return NULL;
    }
    jclass globalRef = (jclass)pJavaEnv->NewGlobalRef(clazz);
    DeleteLocalRef(pJavaEnv, clazz);
    return globalRef;
}

// ...
