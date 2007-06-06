#if !defined(AFX_SETNUMCHANSANDMODESDLG_H__495FE9DF_EE57_439E_AB71_D78AF29D2AFD__INCLUDED_)
#define AFX_SETNUMCHANSANDMODESDLG_H__495FE9DF_EE57_439E_AB71_D78AF29D2AFD__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000
// SetNumChansAndModesDlg.h : header file
//

/////////////////////////////////////////////////////////////////////////////
// CSetNumChansAndModesDlg dialog

class CSetNumChansAndModesDlg : public CDialog
{
// Construction
public:
	CSetNumChansAndModesDlg(CWnd* pParent = NULL);   // standard constructor

// Dialog Data
	//{{AFX_DATA(CSetNumChansAndModesDlg)
	enum { IDD = IDD_NUMCHANSANDMODES };
	UINT	m_num_chans;
	BOOL	m_bBinaryMode;
	BOOL	m_bRealTimeMode;
	//}}AFX_DATA


// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CSetNumChansAndModesDlg)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:

	// Generated message map functions
	//{{AFX_MSG(CSetNumChansAndModesDlg)
		// NOTE: the ClassWizard will add member functions here
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_SETNUMCHANSANDMODESDLG_H__495FE9DF_EE57_439E_AB71_D78AF29D2AFD__INCLUDED_)
