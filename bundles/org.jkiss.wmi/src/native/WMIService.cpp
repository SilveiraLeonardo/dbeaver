
// WMISensor.cpp : Defines the entry point for the DLL application.
//

#include "stdafx.h"
#include "WMIService.h"
#include "WMIObject.h"
#include "WMIObjectSink.h"
#include "WMIUtils.h"

#include <vector>
#include <algorithm>
#include <locale>

#define FIELD_NAME_SERVICE_HANDLE ("serviceHandle")

class WMIThreadInfo {
public:
	DWORD nThreadId;
	JNIEnv* pThreadEnv;
	ObjectSinkVector sinks;
};

typedef std::vector< WMIThreadInfo* > ThreadInfoVector;

static CComCriticalSection csSinkThreads;
JavaVM* WMIService::pJavaVM = nullptr;
//static ThreadInfoVector threadInfos;

WMIService::WMIService(JNIEnv* pJavaEnv, jobject javaObject)
{
	serviceJavaObject = pJavaEnv->NewGlobalRef(javaObject);
	if (!pJavaEnv->ExceptionCheck()) {
		pJavaEnv->SetLongField(serviceJavaObject, JNIMetaData::GetMetaData(pJavaEnv).wmiServiceHandleField, (jlong)this);
	}

	{
		CComCritSecLock<CComCriticalSection> guard(csSinkThreads);
		if (pJavaVM == nullptr) {
			pJavaEnv->GetJavaVM(&pJavaVM);
			_ASSERT(pJavaVM != nullptr);
		}

	}
}

WMIService::~WMIService()
{
}

WMIService* WMIService::GetFromObject(JNIEnv* pJavaEnv, jobject javaObject)
{
	jclass objectClass = pJavaEnv->GetObjectClass(javaObject);
	jfieldID fid = pJavaEnv->GetFieldID(objectClass, "serviceHandle", "J");
	DeleteLocalRef(pJavaEnv, objectClass);
	_ASSERT(fid != NULL);
	if (fid == NULL) {
		return nullptr;
	}
	return reinterpret_cast<WMIService*>(pJavaEnv->GetLongField(javaObject, fid));
}

void WMIService::Connect(
	JNIEnv* pJavaEnv,
	LPWSTR domain, 
	LPWSTR host, 
	LPWSTR user, 
	LPWSTR password,
	LPWSTR locale,
	LPWSTR resource)
{
	if (this->ptrWbemServices != nullptr) {
		THROW_COMMON_EXCEPTION(L"WMI Locator was already initialized");
		return;
	}

	CComPtr<IWbemLocator> ptrWbemLocator;
	HRESULT hres = CoCreateInstance(
		//CLSID_WbemAdministrativeLocator,
		CLSID_WbemLocator,
		0, 
		CLSCTX_INPROC_SERVER | CLSCTX_LOCAL_SERVER, 
		IID_IWbemLocator, 
		reinterpret_cast<LPVOID*>(&ptrWbemLocator));
	if (FAILED(hres)) {
		THROW_COMMON_ERROR(L"Failed to create IWbemLocator object", hres);
		return;
	}

	std::wstring_convert<std::codecvt_utf8_utf16<wchar_t>> converter;
	std::locale loc;
	std::wstring sysLocale(converter.from_bytes(std::use_facet<std::ctype<char>>(loc).widen("")));

	CComBSTR resourceURI;
	if (resource != NULL) {
		resourceURI.Append(L"\\\\");
		if (host != NULL) {
			resourceURI.Append(host);
		} else {
			resourceURI.Append(L".");
		}
		if (resource[0] != '\\') {
			resourceURI.Append(L"\\");
		}
		resourceURI.Append(resource);
	}
	CComBSTR resourceDomain;
	if (domain != NULL) {
		resourceDomain.Append(L"NTLMDOMAIN:");
		resourceDomain.Append(domain);
	}
	hres = ptrWbemLocator->ConnectServer(
		resourceURI,
		user,				// User name
		password,		// User password
		locale == NULL ? CComBSTR(sysLocale.c_str()) : locale,	// Locale
		NULL,                           // Security flags
		resourceDomain,					// Authority
		0,                              // Context object
		&ptrWbemServices                  // IWbemServices proxy
		);
	if (FAILED(hres)) {
		THROW_COMMON_ERROR(L"Failed to connect to WMI Service", hres);
		return;
	}

	hres = CoSetProxyBlanket(
		ptrWbemServices,					// Indicates the proxy to set
		RPC_C_AUTHN_WINNT,           // RPC_C_AUTHN_xxx
		RPC_C_AUTHZ_NONE,            // RPC_C_AUTHZ_xxx
		NULL,                        // Server principal name 
		RPC_C_AUTHN_LEVEL_CALL,      // RPC_C_AUTHN_LEVEL_xxx 
		RPC_C_IMP_LEVEL_IMPERSONATE, // RPC_C_IMP_LEVEL_xxx
		NULL,                        // client identity
		EOAC_NONE                    // proxy capabilities 
		);
	if (FAILED(hres)) {
		THROW_COMMON_ERROR(L"Can't set proxy blanket", hres);
		return;
	}

	//WriteLog(pJavaEnv, LT_DEBUG, bstr_t("WMI Service connected to ") + (LPCWSTR)resource);

}

// ... rest of the code remains the same
