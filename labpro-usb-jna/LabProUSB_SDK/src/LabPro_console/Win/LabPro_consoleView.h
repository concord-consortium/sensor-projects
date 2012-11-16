/*********************************************************************************

Copyright (c) 2012, Vernier Software & Technology
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of Vernier Software & Technology nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL VERNIER SOFTWARE & TECHNOLOGY BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

**********************************************************************************/
// LabPro_consoleView.h : interface of the CLabPro_consoleView class
//
/////////////////////////////////////////////////////////////////////////////

#if !defined(AFX_LABPRO_CONSOLEVIEW_H__29153F26_3D6C_4009_9792_D64C23E59E02__INCLUDED_)
#define AFX_LABPRO_CONSOLEVIEW_H__29153F26_3D6C_4009_9792_D64C23E59E02__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#define REPORT_RECORD_DATA_LENGTH 32

class CLabPro_consoleView : public CListView
{
protected: // create from serialization only
	CLabPro_consoleView();
	DECLARE_DYNCREATE(CLabPro_consoleView)

// Attributes
public:
	CLabPro_consoleDoc* GetDocument();

// Operations
public:
	void RecordLabProInput(LPCSTR pBuf, int buf_len);
	void RecordLabProOutput(LPCSTR pBuf, int buf_len);

// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CLabPro_consoleView)
	public:
	virtual void OnDraw(CDC* pDC);  // overridden to draw this view
	virtual BOOL PreCreateWindow(CREATESTRUCT& cs);
	virtual void OnInitialUpdate();
	protected:
	virtual void OnActivateView(BOOL bActivate, CView* pActivateView, CView* pDeactiveView);
	//}}AFX_VIRTUAL

// Implementation
public:
	virtual ~CLabPro_consoleView();
#ifdef _DEBUG
	virtual void AssertValid() const;
	virtual void Dump(CDumpContext& dc) const;
#endif

protected:
	void RecordLabProString(LPCSTR pBuf, int buf_len, LPCSTR label);

// Generated message map functions
protected:
	//{{AFX_MSG(CLabPro_consoleView)
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};

#ifndef _DEBUG  // debug version in LabPro_consoleView.cpp
inline CLabPro_consoleDoc* CLabPro_consoleView::GetDocument()
   { return (CLabPro_consoleDoc*)m_pDocument; }
#endif

/////////////////////////////////////////////////////////////////////////////

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_LABPRO_CONSOLEVIEW_H__29153F26_3D6C_4009_9792_D64C23E59E02__INCLUDED_)
