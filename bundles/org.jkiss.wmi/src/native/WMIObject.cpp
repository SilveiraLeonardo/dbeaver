
#include "stdafx.h"
#include "WMIObject.h"
#include "WMIUtils.h"

#include <stdexcept>
#include <iostream>

WMIObject::WMIObject(JNIEnv * pJavaEnv, jobject javaObject, IWbemClassObject* pClassObject) :
    ptrClassObject(pClassObject)
{
    pJavaEnv->SetLongField(javaObject, JNIMetaData::GetMetaData(pJavaEnv).wmiObjectHandleField, (jlong)this);
}

WMIObject::~WMIObject()
{
    
}

void WMIObject::Release(JNIEnv* pJavaEnv, jobject javaObject)
{
    if (javaObject != NULL) {
        pJavaEnv->SetLongField(javaObject, JNIMetaData::GetMetaData(pJavaEnv).wmiObjectHandleField, 0l);
    }
    ptrClassObject = NULL;
}

WMIObject* WMIObject::GetFromObject(JNIEnv* pJavaEnv, jobject javaObject)
{
    jclass objectClass = pJavaEnv->GetObjectClass(javaObject);
    jfieldID fid = pJavaEnv->GetFieldID(objectClass, "objectHandle", "J");

    if (objectClass != NULL) {
        pJavaEnv->DeleteLocalRef(objectClass);
    }

    if (fid == NULL) {
        throw std::runtime_error("Failed to get field ID!");
    }
    
    return (WMIObject*)pJavaEnv->GetLongField(javaObject, fid);
}

...

void WMIObject::ReadAttributes(JNIEnv* pJavaEnv, jobject javaObject, LONG lFlags, jobject propList)
{
    JNIMetaData& jniMeta = JNIMetaData::GetMetaData(pJavaEnv);

    ...

    for (;;) {
        ...
        if (hres == WBEM_S_NO_MORE_DATA) {
            break;
        }
        jstring javaPropName = ::MakeJavaString(pJavaEnv, propName);

        if (javaPropName == NULL) {
            std::cerr << "Failed to create Java string from prop name." << std::endl;
            continue;
        }
        ...
        if (pJavaEnv->ExceptionCheck()) {
            break;
        }
    }

    ...
}

...
