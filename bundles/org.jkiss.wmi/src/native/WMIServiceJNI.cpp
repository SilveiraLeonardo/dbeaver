
#include <mutex>
#include <memory>
#include "stdafx.h"
#include "WMIServiceJNI.h"
#include "WMIService.h"
#include "WMIUtils.h"

std::mutex global_mutex;

JNIEXPORT jobject JNICALL Java_org_jkiss_wmi_service_WMIService_connect(
    JNIEnv* pJavaEnv, 
    jclass serviceClass,
    jstring domain, 
    jstring host, 
    jstring user, 
    jstring password,
    jstring locale,
    jstring resource)
{
    if (!pJavaEnv) {
        // pJavaEnv is null
        return NULL;
    }
    
    if (!WMIInitializeThread(pJavaEnv)) return NULL;

    JNIMetaData& jniMeta = JNIMetaData::GetMetaData(pJavaEnv);
    jobject newServiceObject = pJavaEnv->NewObject(jniMeta.wmiServiceClass, jniMeta.wmiServiceConstructor);
    if (pJavaEnv->ExceptionCheck()) {
        return NULL;
    }

    std::unique_lock<std::mutex> lock(global_mutex);
    std::shared_ptr<WMIService> pService = std::make_shared<WMIService>(pJavaEnv, newServiceObject);
    lock.unlock();

    // ...
    
    return newServiceObject;
}

JNIEXPORT void JNICALL Java_org_jkiss_wmi_service_WMIService_close
  (JNIEnv * pJavaEnv, jobject object)
{
    if (!pJavaEnv) {
        // pJavaEnv is null
        return;
    }

    if (!WMIInitializeThread(pJavaEnv)) return;

    std::unique_lock<std::mutex> lock(global_mutex);
    std::shared_ptr<WMIService> pService = WMIService::GetFromObject(pJavaEnv, object);
    lock.unlock();

    if (pService) {
        pService->Release(pJavaEnv);
    }
}

// Similar modifications for all other JNIEXPORT functions
