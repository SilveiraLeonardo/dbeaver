
#include "StdAfx.h"
#include "WMIObjectSink.h"
#include "WMIUtils.h"

static const long MAX_CACHE_SIZE = 1000;

WMIObjectSink::WMIObjectSink() :
	pService(NULL),
	javaSinkObject(NULL)
{
}

void WMIObjectSink::InitSink(WMIService* pSvc, JNIEnv* pJavaEnv, jobject javaObject)
{
	_ASSERT(pSvc != NULL);
	_ASSERT(javaObject != NULL);

	pService = pSvc;
	javaSinkObject = pJavaEnv->NewGlobalRef(javaObject);
}

void WMIObjectSink::TermSink(JNIEnv* pJavaEnv)
{
	if (javaSinkObject != NULL) {
		pJavaEnv->DeleteGlobalRef(javaSinkObject);
		javaSinkObject = NULL;
	}
}

WMIObjectSink::~WMIObjectSink(void)
{
}

void WMIObjectSink::FlushObjectsCache(JNIEnv* pJavaEnv)
{
	JNIMetaData& jniMeta = JNIMetaData::GetMetaData(pJavaEnv);
	JavaObjectVector objects;
	for (size_t i = 0; i < objectsCache.size(); i++) {
		objects.push_back(pService->MakeWMIObject(pJavaEnv, objectsCache[i]));
	}
	objectsCache.clear();
	jobjectArray javaArray = ::MakeJavaArrayFromVector(pJavaEnv, jniMeta.wmiObjectClass, objects);

	pJavaEnv->CallVoidMethod(
		javaSinkObject, 
		jniMeta.wmiObjectSinkIndicateMethod,
		javaArray);

	DeleteLocalRef(pJavaEnv, javaArray);

	if (pJavaEnv->ExceptionCheck()) {
		//pService->WriteLog(pJavaEnv, LT_ERROR, L"Can't call indicate for object sink");
		pJavaEnv->ExceptionClear();
	}
}

HRESULT WMIObjectSink::Indicate( 
    long lObjectCount,
    IWbemClassObject **ppClassObject)
{
	if (lObjectCount <= 0) {
		return WBEM_S_NO_ERROR;
	}
	for (long i = 0; i < lObjectCount; i++) {
		if (objectsCache.size() < MAX_CACHE_SIZE) {
			objectsCache.push_back(ppClassObject[i]);
		} else {
			break;
		}
	}
	if (objectsCache.size() >= MAX_CACHE_SIZE) {
		JNIEnv* pJavaEnv = NULL;
		WMIService::GetJavaVM()->AttachCurrentThread((void**)&pJavaEnv, NULL);
		_ASSERT(pJavaEnv != NULL);
		if (pJavaEnv != NULL) {
			FlushObjectsCache(pJavaEnv);
			WMIService::GetJavaVM()->DetachCurrentThread();
		}
	}

	return WBEM_S_NO_ERROR;
}

HRESULT WMIObjectSink::SetStatus( 
    long lFlags,
    HRESULT hResult,
    BSTR strParam,
    IWbemClassObject *pClassObject)
{
	// Remainder of the code...
}
