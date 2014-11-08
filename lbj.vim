" Vim syntax file
" Language:     LBJ
" Maintainer:   Nick Rizzolo <rizzolo@gmail.com>
" URL:          http://l2r.cs.uiuc.edu/~cogcomp
" Last Change:  2009 Mar 30

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
  command! -nargs=+ LBJHiLink hi link <args>
else
  command! -nargs=+ LBJHiLink hi def link <args>
endif

syn clear javaError
syn clear javaError2
syn clear javaClassDecl
" Make sure it exists before trying to clear it.
syn match javaAnnotation "fail"
syn clear javaAnnotation

syn match   lbjError      "[`]"
syn match   lbjError      "<<<\|<>\|||=\|&&=\|\*\/\|#\|=<"
" syn match   lbjError      "<<<\|\.\.\|<>\|||=\|&&=\|[^-]->\|\*\/\|#\|=<"
syn keyword lbjError      goto const enum
syn match   lbjError      "\(^\s*\|[^\.\S]\s*\)\@<=class\>"

if !exists("java_allow_cpp_keywords")
  " The default used to be to highlight C++ keywords.  But several people
  " don't like that, so default to not highlighting these.
  let java_allow_cpp_keywords = 1
endif
if !java_allow_cpp_keywords
  syn keyword lbjError  auto delete extern friend inline redeclared
  syn keyword lbjError  register signed sizeof struct template typedef union
  syn keyword lbjError  unsigned operator
endif

syn keyword lbjOperator   forall exists atleast atmost in of normalizedby
syn keyword lbjType       discrete real mixed constraint cached cachedin cachedinmap
syn keyword lbjStatement  sense senseall

LBJHiLink lbjError     javaError
LBJHiLink lbjOperator  javaOperator
LBJHiLink lbjType      javaType
LBJHiLink lbjStatement javaStatement

syn match   lbjConstraint       "@\h[\w\.]*\>"
syn keyword lbjInferenceDecl    inference head
syn keyword lbjLearn            learn using from rounds with encoding testFrom evaluate cval prune progressOutput preExtract end
syn keyword lbjInferenceClauses subjectto maximize minimize

syn cluster javaTop add=lbjConstraint,lbjOperator,lbjType,lbjInferenceDecl,lbjStatement,lbjLearn,lbjInferenceClauses,lbjError

if version >= 508 || !exists("did_java_syn_inits")
  if version < 508
    let did_java_syn_inits = 1
  endif
  LBJHiLink lbjConstraint              Special
  LBJHiLink lbjInferenceDecl           javaStorageClass
  LBJHiLink lbjLearn                   Identifier
  LBJHiLink lbjInferenceClauses        Identifier
endif

delcommand LBJHiLink

let b:current_syntax = "lbj"

