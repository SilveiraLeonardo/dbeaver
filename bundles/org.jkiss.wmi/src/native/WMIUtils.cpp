
// WMISensor.cpp : Defines the entry point for the DLL application.
//

#include "stdafx.h"
#include "WMIUtils.h"
#include "JNIMetaData.h"
#include <vector> // Include the vector library

HMODULE hWMIUtils;
HMODULE hWbemCommon;

// ... (Other functions remain unchanged)

void FormatErrorMessage(LPCWSTR message, HRESULT error, BSTR* pBuffer)
{
  if(error == NO_ERROR) {
    error = GetLastError();
  }
  _bstr_t finalMessage = message;
  finalMessage += L" - ";
  {
    std::vector<TCHAR> systemMessage(1024);

    // Get system message for last error code
    DWORD count = ::FormatMessage(  
      FORMAT_MESSAGE_FROM_SYSTEM,
      NULL,
      error,
      MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT),
      systemMessage.data(),
      static_cast<DWORD>(systemMessage.size()) - 1,
      NULL
    );

    // ... (Rest of the code remains unchanged)
  }
}

// ... (Rest of the code remains unchanged)
