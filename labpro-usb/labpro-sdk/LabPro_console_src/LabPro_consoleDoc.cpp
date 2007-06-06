// LabPro_consoleDoc.cpp : implementation of the CLabPro_consoleDoc class
//

#include "stdafx.h"
#include "LabPro_console.h"

#include "LabPro_consoleDoc.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CLabPro_consoleDoc

IMPLEMENT_DYNCREATE(CLabPro_consoleDoc, CDocument)

BEGIN_MESSAGE_MAP(CLabPro_consoleDoc, CDocument)
	//{{AFX_MSG_MAP(CLabPro_consoleDoc)
		// NOTE - the ClassWizard will add and remove mapping macros here.
		//    DO NOT EDIT what you see in these blocks of generated code!
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CLabPro_consoleDoc construction/destruction

CLabPro_consoleDoc::CLabPro_consoleDoc()
{
	m_LabPro_nNumChannels = 1;
	m_LabPro_bBinaryMode = 0;
	m_LabPro_bRealTime = 0;
	m_input_buffer_next_byte_index = 0;
}

CLabPro_consoleDoc::~CLabPro_consoleDoc()
{
}

BOOL CLabPro_consoleDoc::OnNewDocument()
{
	if (!CDocument::OnNewDocument())
		return FALSE;

	// TODO: add reinitialization code here
	// (SDI documents will reuse this document)

	return TRUE;
}



/////////////////////////////////////////////////////////////////////////////
// CLabPro_consoleDoc serialization

void CLabPro_consoleDoc::Serialize(CArchive& ar)
{
	if (ar.IsStoring())
	{
		// TODO: add storing code here
	}
	else
	{
		// TODO: add loading code here
	}
}

/////////////////////////////////////////////////////////////////////////////
// CLabPro_consoleDoc diagnostics

#ifdef _DEBUG
void CLabPro_consoleDoc::AssertValid() const
{
	CDocument::AssertValid();
}

void CLabPro_consoleDoc::Dump(CDumpContext& dc) const
{
	CDocument::Dump(dc);
}
#endif //_DEBUG

/////////////////////////////////////////////////////////////////////////////
// CLabPro_consoleDoc commands
