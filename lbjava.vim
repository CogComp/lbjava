" Vim syntax file
" Language:     LBJava
" Maintainer:   Nick Rizzolo <rizzolo@gmail.com>
" URL:          http://cogcomp.cs.illinois.edu/page/software_view/LBJ
" Last Change:  2015 May 6

if version < 600
  syntax clear
elseif exists("b:current_syntax")
  finish
endif

" source the java.vim file
if version < 600
  source $VIMRUNTIME/syntax/java.vim
else
  runtime! syntax/java.vim
endif
unlet b:current_syntax

" don't use standard HiLink, it will not work with included syntax files
if version < 508
  command! -nargs=+ LBJavaHiLink hi link <args>
else
  command! -nargs=+ LBJavaHiLink hi def link <args>
endif

syn clear javaError
syn clear javaError2
syn clear javaClassDecl
" Make sure it exists before trying to clear it.
syn match javaAnnotation "fail"
syn clear javaAnnotation

syn match   lbjavaError      "[`]"
syn match   lbjavaError      "<<<\|<>\|||=\|&&=\|\*\/\|#\|=<"
" syn match   lbjavaError      "<<<\|\.\.\|<>\|||=\|&&=\|[^-]->\|\*\/\|#\|=<"
syn keyword lbjavaError      goto const enum
syn match   lbjavaError      "\(^\s*\|[^\.\S]\s*\)\@<=class\>"

if !exists("java_allow_cpp_keywords")
  " The default used to be to highlight C++ keywords.  But several people
  " don't like that, so default to not highlighting these.
  let java_allow_cpp_keywords = 1
endif
if !java_allow_cpp_keywords
  syn keyword lbjavaError  auto delete extern friend inline redeclared
  syn keyword lbjavaError  register signed sizeof struct template typedef union
  syn keyword lbjavaError  unsigned operator
endif

syn keyword lbjavaOperator   forall exists atleast atmost in of normalizedby
syn keyword lbjavaType       discrete real mixed constraint cached cachedin cachedinmap
syn keyword lbjavaStatement  sense senseall

LBJavaHiLink lbjavaError     javaError
LBJavaHiLink lbjavaOperator  javaOperator
LBJavaHiLink lbjavaType      javaType
LBJavaHiLink lbjavaStatement javaStatement

syn match   lbjavaConstraint       "@\h[\w\.]*\>"
syn keyword lbjavaInferenceDecl    inference head
syn keyword lbjavaLearn            learn using from rounds with encoding testFrom evaluate cval prune progressOutput preExtract end
syn keyword lbjavaInferenceClauses subjectto maximize minimize

syn cluster javaTop add=lbjavaConstraint,lbjavaOperator,lbjavaType,lbjavaInferenceDecl,lbjavaStatement,lbjavaLearn,lbjavaInferenceClauses,lbjavaError

if version >= 508 || !exists("did_java_syn_inits")
  if version < 508
    let did_java_syn_inits = 1
  endif
  LBJavaHiLink lbjavaConstraint              Special
  LBJavaHiLink lbjavaInferenceDecl           javaStorageClass
  LBJavaHiLink lbjavaLearn                   Identifier
  LBJavaHiLink lbjavaInferenceClauses        Identifier
endif

delcommand LBJavaHiLink

let b:current_syntax = "lbjava"

