// LabPro_consoleDoc.h : interface of the CLabPro_consoleDoc class
//
/////////////////////////////////////////////////////////////////////////////

#if !defined(AFX_LABPRO_CONSOLEDOC_H__76256924_B0C2_470C_8D31_E8174588B9AF__INCLUDED_)
#define AFX_LABPRO_CONSOLEDOC_H__76256924_B0C2_470C_8D31_E8174588B9AF__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#define MAX_INPUT_BUFLEN 4000

class CLabPro_consoleDoc : public CDocument
{
protected: // create from serialization only
	CLabPro_consoleDoc();
	DECLARE_DYNCREATE(CLabPro_consoleDoc)

// Attributes
public:
//LabPro parms:
	int m_LabPro_nNumChannels;
	short m_LabPro_bBinaryMode;
	short m_LabPro_bRealTime;

// Operations
public:

// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CLabPro_consoleDoc)
	public:
	virtual BOOL OnNewDocument();
	virtual void Serialize(CArchive& ar);
	//}}AFX_VIRTUAL

// Implementation
public:
	virtual ~CLabPro_consoleDoc();
#ifdef _DEBUG
	virtual void AssertValid() const;
	virtual void Dump(CDumpContext& dc) const;
#endif

	char m_input_buffer[MAX_INPUT_BUFLEN];
	int m_input_buffer_next_byte_index;

protected:

// Generated message map functions
protected:
	//{{AFX_MSG(CLabPro_consoleDoc)
		// NOTE - the ClassWizard will add and remove member functions here.
		//    DO NOT EDIT what you see in these blocks of generated code !
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};

/////////////////////////////////////////////////////////////////////////////

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_LABPRO_CONSOLEDOC_H__76256924_B0C2_470C_8D31_E8174588B9AF__INCLUDED_)
