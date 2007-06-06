// LabPro_consoleView.cpp : implementation of the CLabPro_consoleView class
//

#include "stdafx.h"
#include "LabPro_console.h"

#include "LabPro_consoleDoc.h"
#include "MainFrm.h"
#include "LabPro_consoleView.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CLabPro_consoleView

IMPLEMENT_DYNCREATE(CLabPro_consoleView, CListView)

BEGIN_MESSAGE_MAP(CLabPro_consoleView, CListView)
	//{{AFX_MSG_MAP(CLabPro_consoleView)
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CLabPro_consoleView construction/destruction

CLabPro_consoleView::CLabPro_consoleView()
{
	// TODO: add construction code here

}

CLabPro_consoleView::~CLabPro_consoleView()
{
}

BOOL CLabPro_consoleView::PreCreateWindow(CREATESTRUCT& cs)
{
	cs.style &= ~LVS_TYPEMASK;
	cs.style |= LVS_REPORT;

	return CListView::PreCreateWindow(cs);
}


/////////////////////////////////////////////////////////////////////////////
// CLabPro_consoleView drawing

void CLabPro_consoleView::OnDraw(CDC* pDC)
{
	CLabPro_consoleDoc* pDoc = GetDocument();
	ASSERT_VALID(pDoc);
	// TODO: add draw code for native data here
}

/////////////////////////////////////////////////////////////////////////////
// CLabPro_consoleView diagnostics

#ifdef _DEBUG
void CLabPro_consoleView::AssertValid() const
{
	CListView::AssertValid();
}

void CLabPro_consoleView::Dump(CDumpContext& dc) const
{
	CListView::Dump(dc);
}

CLabPro_consoleDoc* CLabPro_consoleView::GetDocument() // non-debug version is inline
{
	ASSERT(m_pDocument->IsKindOf(RUNTIME_CLASS(CLabPro_consoleDoc)));
	return (CLabPro_consoleDoc*)m_pDocument;
}
#endif //_DEBUG

/////////////////////////////////////////////////////////////////////////////
// CLabPro_consoleView message handlers

#define NUM_COLUMNS 5

static _TCHAR *_gszColumnLabel[NUM_COLUMNS] =
{
	_T("Time"), _T("IN/OUT"), _T("# bytes"), _T("Ascii"), _T("Hex")
};

static int _gnColumnFmt[NUM_COLUMNS] =
{
	LVCFMT_RIGHT, LVCFMT_RIGHT, LVCFMT_RIGHT, LVCFMT_LEFT, LVCFMT_LEFT
};

static int _gnColumnWidth[NUM_COLUMNS] =
{
	80, 60, 60, 300, 700
};

void CLabPro_consoleView::OnInitialUpdate() 
{
	CListView::OnInitialUpdate();
	
	CListCtrl& ListCtrl = GetListCtrl();

	int i;
	LV_COLUMN lvc;

	lvc.mask = LVCF_FMT | LVCF_WIDTH | LVCF_TEXT | LVCF_SUBITEM;

	for(i = 0; i<NUM_COLUMNS; i++)
	{
		lvc.iSubItem = i;
		lvc.pszText = _gszColumnLabel[i];
		lvc.cx = _gnColumnWidth[i];
		lvc.fmt = _gnColumnFmt[i];
		ListCtrl.InsertColumn(i,&lvc);
	}

//	RecordLabProOutput("abra ka dabra", lstrlen("abra ka dabra"));
//	RecordLabProInput("hocus pocus", lstrlen("hocus pocus"));
}

void CLabPro_consoleView::RecordLabProOutput(LPCSTR pBuf, int buf_len)
{
	RecordLabProString(pBuf, buf_len, "OUT");
}

void CLabPro_consoleView::RecordLabProInput(LPCSTR pBuf, int buf_len)
{
	RecordLabProString(pBuf, buf_len, "IN");
}

void CLabPro_consoleView::RecordLabProString(LPCSTR pBuf, int buf_len, LPCSTR label)
{
	CListCtrl& ListCtrl = GetListCtrl();
	CTime theTime;
	theTime = CTime::GetCurrentTime();
	CString time_string = theTime.Format("%H:%M:%S");
	int new_index = ListCtrl.GetItemCount();
	int i, record_len;
	CString short_string;
	char tmpstring[30];

	while (buf_len > 0)
	{
		ListCtrl.InsertItem(new_index, (LPCSTR) time_string);
		ListCtrl.SetItemText(new_index, 1, label);
		record_len = buf_len;
		if (record_len > REPORT_RECORD_DATA_LENGTH)
			record_len = REPORT_RECORD_DATA_LENGTH;
		wsprintf(tmpstring, "%d", record_len);
		ListCtrl.SetItemText(new_index, 2, tmpstring);
		short_string = CString(pBuf, record_len);
		ListCtrl.SetItemText(new_index, 3, (LPCSTR) short_string);
		short_string = "";
		for (i = 0; i < record_len; i++)
		{
			wsprintf(tmpstring, "%02x ", (BYTE) (pBuf[i]));
			short_string += tmpstring;
		}
		ListCtrl.SetItemText(new_index, 4, (LPCSTR) short_string);

		buf_len -= record_len;
		pBuf += record_len;
		new_index++;
	}
}


void CLabPro_consoleView::OnActivateView(BOOL bActivate, CView* pActivateView, CView* pDeactiveView) 
{
	CListView::OnActivateView(bActivate, pActivateView, pDeactiveView);

	CMainFrame *pMainWnd = (CMainFrame *) AfxGetMainWnd();
	if (pMainWnd && bActivate)
	{
		CWnd *pEdit = pMainWnd->m_wndSendBar.GetDlgItem(IDC_EDIT_LABPRO_XMIT_STRING);
		if (pEdit)
			pEdit->SetFocus();	
	}
}
