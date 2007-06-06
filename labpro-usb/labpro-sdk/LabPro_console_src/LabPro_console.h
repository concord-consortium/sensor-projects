// LabPro_console.h : main header file for the LABPRO_CONSOLE application
//

#if !defined(AFX_LABPRO_CONSOLE_H__51E5A1E2_8189_4594_B026_19BE79EE43A0__INCLUDED_)
#define AFX_LABPRO_CONSOLE_H__51E5A1E2_8189_4594_B026_19BE79EE43A0__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#ifndef __AFXWIN_H__
	#error include 'stdafx.h' before including this file for PCH
#endif

#include "resource.h"       // main symbols

/////////////////////////////////////////////////////////////////////////////
// CLabPro_consoleApp:
// See LabPro_console.cpp for the implementation of this class
//

class CLabPro_consoleApp : public CWinApp
{
public:
	CLabPro_consoleApp();

// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CLabPro_consoleApp)
	public:
	virtual BOOL InitInstance();
	virtual int ExitInstance();
	//}}AFX_VIRTUAL

// Implementation
	//{{AFX_MSG(CLabPro_consoleApp)
	afx_msg void OnAppAbout();
		// NOTE - the ClassWizard will add and remove member functions here.
		//    DO NOT EDIT what you see in these blocks of generated code !
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};


/////////////////////////////////////////////////////////////////////////////

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_LABPRO_CONSOLE_H__51E5A1E2_8189_4594_B026_19BE79EE43A0__INCLUDED_)
