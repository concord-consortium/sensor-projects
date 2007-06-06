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
