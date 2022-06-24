/*
 * Copyright Studio 42 GmbH 2021. All rights reserved.
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * For details to the License read https://www.s42m.de/license
 */

parser grammar DLTParser;

options { tokenVocab=DLTLexer; }

warpTemplate : ( text | macro | comment | codeBlock )* EOF ;

text : ( STRING | SPECIAL | ESCAPED_DOLLAR | WS | HUNGRY | ESCAPED_HUNGRY | NEWLINE )+ ;

macro : START_MACRO expression END_MACRO ( HUNGRY WS? NEWLINE? )? ;

comment : START_COMMENT ( COMMENT_STRING | COMMENT_SPECIAL | ESCAPED_END_COMMENT )* END_COMMENT ( HUNGRY WS? NEWLINE? )? ;

codeBlock : START_CODE code END_CODE ( HUNGRY WS? NEWLINE? )? ;
code : ( CODE_STRING | CODE_SPECIAL | ESCAPED_END_CODE )* ;

expression
	: expressionRead | expressionSet | expressionUnset | expressionInc
	| expressionDec | expressionIf | expressionIfnot | expressionElse
	| expressionElseIf | expressionElseIfNot | expressionFor | expressionEndIf
	| expressionEndIfNot | expressionEndFor | expressionAssert | expressionInclude
	| expressionLoad | expressionSection | expressionEndSection ;

expressionRead : KEYWORD ( parameter )+ | keyword ;
expressionSet : KEYWORD_SET path ( assignment )+ ;
expressionSection : KEYWORD_SECTION KEYWORD ;
expressionEndSection : KEYWORD_END_SECTION ;
expressionLoad : KEYWORD_LOAD KEYWORD MACRO_STRING ( parameter )* ;
expressionUnset : KEYWORD_UNSET KEYWORD ;
expressionInc : KEYWORD_INC KEYWORD ;
expressionDec : KEYWORD_DEC KEYWORD ;
expressionIf : KEYWORD_IF keyword ( comparator )? ;
expressionElseIf : KEYWORD_ELSEIF keyword ( comparator )? ;
expressionElseIfNot : KEYWORD_ELSEIFNOT keyword ( comparator )? ;
expressionElse : KEYWORD_ELSE ;
expressionIfnot : KEYWORD_IFNOT keyword ( comparator )? ;
expressionFor : KEYWORD_FOR forIterator forArray ;
expressionEndIf : KEYWORD_END_IF ;
expressionEndIfNot : KEYWORD_END_IFNOT ;
expressionEndFor : KEYWORD_END_FOR ;
expressionAssert : KEYWORD_ASSERT keyword MACRO_STRING ;
expressionInclude : KEYWORD_INCLUDE inclusion ;

parameter : ( keyword | MACRO_STRING | MACRO_NUMBER ) ;
assignment : ( keyword | MACRO_STRING | MACRO_NUMBER ) ;
comparator : ( keyword | MACRO_STRING | MACRO_NUMBER ) ;
forArray : keyword ;
forIterator : keyword ;
inclusion : ( keyword | MACRO_STRING ) ;

keyword : path modifiers ;
path : KEYWORD ( PATH_SEPARATOR KEYWORD )* ;
modifiers : ( MODIFIER_SEPARATOR modifier )* ;
modifier : KEYWORD ;