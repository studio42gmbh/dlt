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

lexer grammar DLTLexer;

START_CODE :			'${$' -> pushMode(CODE) ;
START_COMMENT :			'${#' -> pushMode(COMMENT) ;
START_MACRO :			'${' -> pushMode(MACRO) ;

SPECIAL :				'\\' | '$' ;
ESCAPED_DOLLAR :		'\\$' {setText("$");} ;
ESCAPED_HUNGRY :		'\\>' {setText(">");} ;

STRING :				~[>\\$\t\f\n\r ]+ ;
WS :					[ \t]+ ;
NEWLINE :				([\r]? [\n]) ;
HUNGRY :				'>' ;


mode COMMENT;

ESCAPED_END_COMMENT :	'\\}' {setText("}");} ;
COMMENT_SPECIAL :		'\\' ;
COMMENT_STRING :		~[\\}]+ ;
END_COMMENT :			'}' -> popMode ;


mode CODE;

ESCAPED_END_CODE :		'\\}' {setText("}");} ;
CODE_SPECIAL :			'\\' ;
CODE_STRING :			~[\\}]+ ;
END_CODE :				'}' -> popMode ;


mode MACRO;

END_MACRO :				'}' -> popMode ;

KEYWORD_INCLUDE :		'include' ;
KEYWORD_LOAD :			'load' ;
KEYWORD_ASSERT :		'assert' ;
KEYWORD_SET :			'set' ;
KEYWORD_UNSET :			'unset' ;
KEYWORD_INC :			'inc' ;
KEYWORD_DEC :			'dec' ;

KEYWORD_CODE :			'code' ;

KEYWORD_SECTION :		'section' ;
KEYWORD_END_SECTION :	'/section' ;

KEYWORD_IF :			'if' ;
KEYWORD_END_IF :		'/if' ;
KEYWORD_ELSE :			'else' ;
KEYWORD_ELSEIF :		'elseif' ;
KEYWORD_ELSEIFNOT :		'elseifnot' ;
KEYWORD_IFNOT :			'ifnot' ;
KEYWORD_END_IFNOT :		'/ifnot' ;

KEYWORD_FOR :			'for' ;
KEYWORD_END_FOR :		'/for' ;

MODIFIER_SEPARATOR :	':' ;
KEYWORD :				[a-zA-Z_#] [a-zA-Z0-9_#]* ;
PATH_SEPARATOR :		'.' ;

MACRO_STRING :			'"' ~["]* '"' ;
MACRO_NUMBER :			[-]? [0-9]+ ( ('.' [0-9]+ ( 'f' )? )? | ( 'L' )?  );
MACRO_WS :				[ \t\n\r]+ -> skip ;
