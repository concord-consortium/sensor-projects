// MainFrm.h : interface of the CMainFrame class
//
/////////////////////////////////////////////////////////////////////////////

#if !defined(AFX_MAINFRM_H__1F01D238_975A_4E78_919F_54298997A179__INCLUDED_)
#define AFX_MAINFRM_H__1F01D238_975A_4E78_919F_54298997A179__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

class CMainFrame : public CFrameWnd
{
	
protected: // create from serialization only
	CMainFrame();
	DECLARE_DYNCREATE(CMainFrame)

// Attributes
public:
	virtual void OnUpdateFrameTitle(BOOL bAddToTitle);

// Operations
public:
	int ShipBytesToLabPro(LPCSTR pBytes, int num_bytes);
	int ShipStringToLabPro(LPCSTR pStr)
	{
		return(ShipBytesToLabPro(pStr, lstrlen(pStr)));
	}

// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CMainFrame)
	public:
	virtual BOOL PreCreateWindow(CREATESTRUCT& cs);
	virtual BOOL DestroyWindow();
	virtual BOOL PreTranslateMessage(MSG* pMsg);
	//}}AFX_VIRTUAL

// Implementation
public:
	virtual ~CMainFrame();
#ifdef _DEBUG
	virtual void AssertValid() const;
	virtual void Dump(CDumpContext& dc) const;
#endif

// control bar embedded members
	CStatusBar  m_wndStatusBar;
//	CToolBar    m_wndToolBar;
	CDialogBar	m_wndSendBar;

// Generated message map functions
protected:
	//{{AFX_MSG(CMainFrame)
	afx_msg int OnCreate(LPCREATESTRUCT lpCreateStruct);
	afx_msg void OnConnect();
	afx_msg void OnUpdateConnect(CCmdUI* pCmdUI);
	afx_msg void OnDisconnect();
	afx_msg void OnUpdateDisconnect(CCmdUI* pCmdUI);
	afx_msg void OnGetstatus();
	afx_msg void OnUpdateGetstatus(CCmdUI* pCmdUI);
	afx_msg void OnReconnect();
	afx_msg void OnUpdateReconnect(CCmdUI* pCmdUI);
	afx_msg void OnSetnumchannels();
	afx_msg void OnUpdateSetnumchannels(CCmdUI* pCmdUI);
	afx_msg void OnTimer(UINT nIDEvent);
	afx_msg void OnEditPaste();
	afx_msg void OnUpdateEditPaste(CCmdUI* pCmdUI);
	//}}AFX_MSG
	afx_msg void OnUpdateSendButton(CCmdUI* pCmdUI);
	afx_msg void OnSendStringToLabPro();
	DECLARE_MESSAGE_MAP()

	int m_timerId;
};

/////////////////////////////////////////////////////////////////////////////

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_MAINFRM_H__1F01D238_975A_4E78_919F_54298997A179__INCLUDED_)
