// SetNumChansAndModesDlg.cpp : implementation file
//

#include "stdafx.h"
#include "LabPro_console.h"
#include "SetNumChansAndModesDlg.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CSetNumChansAndModesDlg dialog


CSetNumChansAndModesDlg::CSetNumChansAndModesDlg(CWnd* pParent /*=NULL*/)
	: CDialog(CSetNumChansAndModesDlg::IDD, pParent)
{
	//{{AFX_DATA_INIT(CSetNumChansAndModesDlg)
	m_num_chans = 0;
	m_bBinaryMode = FALSE;
	m_bRealTimeMode = FALSE;
	//}}AFX_DATA_INIT
}


void CSetNumChansAndModesDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(CSetNumChansAndModesDlg)
	DDX_Text(pDX, IDC_NUM_CHANNELS, m_num_chans);
	DDV_MinMaxUInt(pDX, m_num_chans, 0, 99);
	DDX_Check(pDX, IDC_BINARY_MODE, m_bBinaryMode);
	DDX_Check(pDX, IDC_REAL_TIME_MODE, m_bRealTimeMode);
	//}}AFX_DATA_MAP
}


BEGIN_MESSAGE_MAP(CSetNumChansAndModesDlg, CDialog)
	//{{AFX_MSG_MAP(CSetNumChansAndModesDlg)
		// NOTE: the ClassWizard will add message map macros here
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CSetNumChansAndModesDlg message handlers
