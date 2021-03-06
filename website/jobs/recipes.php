<?php
//
// List all recipes
// written by Haviland Tenouri
//

include ( $_SERVER['DOCUMENT_ROOT'] . "/includes/tools.php" );

$fn = array (
	'skill',	// list only recipes of this skill
	'book',		// list only recipes in this book
	'type',		// list all recipes
	'recipe',	// show recipe details
	'id',		// show recipe of this id
	'resolve',	// resolve all recipe ingredients
	'optional'	// resolve optional recipes, too
);
$pval = array ();
post2pval ( $fn, $pval );
if ( $pval['type'] == "" )
	$pval['type'] = "P";

function resolve_recipe ( $pval, $recipe_name, & $ids, $id_only )
{
	global $mysqli;

	$q = "
		SELECT * FROM recipes WHERE
		name=\"".$recipe_name."\"
	";
	if ( $pval['optional'] == "" )
	{
		$q .= " AND (type='I' or type='i' or type='P') ";
	}
	if ( ( $pval['id'] != "" ) && ( $id_only == true ) )
	{
		$q .= " AND id='".$pval['id']."'";
	}
	$q .= "
		ORDER BY name,level,result,book
	";
	$recipe = $mysqli->query ($q);
	if ( $recipe->num_rows == 0 )
	{
		print "<tr><td colspan='7'>";
		print "'$recipe_name': no recipe found!";
		// print "q: $q";
		print "</td></tr>\n";
		return;
	}
	while ( $r = $recipe->fetch_assoc() )
	{
		$ingredients = explode (",", $r['ingredient']);
		$count = 0;
		$ing   = "";
		$id = $r['id'];
		if ( in_array ( $id, $ids ) )
			return;
		$r_name = $r['name'];
		$r_type = $r['type'];
		$ids[] = $id;
		$recipes = [];
		foreach ($ingredients as $i)
		{
			$prep = trim ($i);
			$pos1 = strpos ( $prep, " " );			$type	= substr ( $prep, 0, $pos1 );		$pos1++;
			$pos2 = strpos ( $prep, " ", $pos1 );	$amount = substr ( $prep, $pos1, $pos2 - $pos1 );	$pos2++;
			$name   = str_replace ( "'","&apos;", substr ( $prep, $pos2 ) );
			if ( ! is_numeric ( $amount ) )
			{
				print "<tr><td colspan='7'>";
				print "<mark>$recipe_name: syntax error: '".$prep."'!</mark>";
				print "</td></tr>\n";
				return;
			}
			$recipes[$name] = $type;
			if ( $type == "C" )	// crafted
			{
				$name = "<a href='/jobs/recipes.php?recipe=$name'>$name ($type)</a>";
			}
			else if ( $type == "H" )	// harvested
			{
				$name = "<a href='/jobs/harvesting.php?plant=$name'>$name</a>";
			}
			else if ( $type == "B" )	// Buy from merchant
			{
				$name = "<a href='/items.php?item=$name'>$name</a>";
			}
			else if ( $type == "M" )	// mined
			{
				$name = "<a href='/jobs/mining.php?item=$name'>$name</a>";
			}
			/*
			 * no page for looting :/
			 */
			else if ( $type == "L" )	// Looted
			{
			}
			else	// unknown type
			{
				$name = "<mark>$name</mark> syntax error";
			}
			if ( $count )
			{
				$ing .= " +<br>";
			}
			$ing .= "$amount ";
			$ing .= $name;
			// $ing .= " ($type/$r_type)";
			$count++;
		}
		if ( ( $r_type == "P" ) || ( $r_type == "I" ) )
				print "<tr><td colspan='7'><b>$r_name ($r_type)</b></td></tr>\n";
		print "<tr>";
			print "<td>$ing</td>\n";
			print "<td>".$r['tool']."</td>\n";
			print "<td>".$r['result']."</td>\n";
			print "<td>";
				print "<a href='".$_SERVER['PHP_SELF']."?recipe=$r_name&id=$id'>$r_name ($r_type)</a>";
			print "</td>\n";
			print "<td>".$r['skill']."</td>\n";
			print "<td>".$r['level']."</td>\n";
			print "<td>";
				$book = str_replace ( ","," +<br>", $r['book'] );
				print "<a href='".$_SERVER['PHP_SELF']."?book=$book'>$book</a>";
			print "</td>";
		print "</tr>";
/*
		if ( $r_type == "P" )
			print "<tr><td colspan='7'><b><hr></b></td></tr>\n";
*/
		if ( $pval['resolve'] != "" )
		{
			foreach ($recipes as $name => $type)
			{
				if ( $type == "C" )	// crafted
				{
						resolve_recipe ( $pval, $name, $ids, false );
				}
			}
		}
	}
}

function print_recipe ( $pval )
{
	global $mysqli;

	$name = $pval['recipe'];
	print "<h3>";
	print " | ";

	href ( "overview", $_SERVER['PHP_SELF'], "" );
	if ( $pval['resolve'] != "" )
	{
		$param = "";
		$param = add_url_param ( $param, "recipe=$name" );
		if ( $pval['id'] != "" )
			$param = add_url_param ( $param, "id=".$pval['id'] );
		$link = "recipe only";
	}
	else
	{
		$param = "";
		$param = add_url_param ( $param, "resolve=true" );
		$param = add_url_param ( $param, "recipe=$name" );
		if ( $pval['id'] != "" )
			$param = add_url_param ( $param, "id=".$pval['id'] );
		$link = "resolve recipe";
	}
	print " | ";
	href ( $link, $_SERVER['PHP_SELF'], $param );
	if ( $pval['optional'] == "" )
	{
		print " | ";
		$param = add_url_param ( $param, "resolve=".$pval['resolve'] );
		$param = add_url_param ( $param, "id=".$pval['id'] );
		$param = add_url_param ( $param, "optional=true" );
		href ( "show optional recipes", $_SERVER['PHP_SELF'], $param );
	}
	else
	{
		print " | ";
		$param = add_url_param ( $param, "resolve=".$pval['resolve'] );
		$param = add_url_param ( $param, "id=".$pval['id'] );
		href ( "without optional ingredients", $_SERVER['PHP_SELF'], $param );
	}
	print " | ";
	print "</h3>";
	print "<h3>$name</h3>\n";
	?>
	<table class='recipe_table hovableTable sortable'>
		<tr>
			<th>Ingredient</th>
			<th>Use Tool</th>
			<th>Amount</th>
			<th>Result</th>
			<th>Skill</th>
			<th>Level</th>
			<th>Book</th>
		</tr>
	<?php
	$ids = array ();
	resolve_recipe ( $pval, str_replace ( "'", "&apos;", $name ), $ids, true );
	?>
	</table>
	<?php
}

function show_recipes ( $pval )
{
	global $mysqli;

	$q = "
		SELECT DISTINCT skill FROM recipes
	";
	if ( $pval['book'] != "" )
		$q .= "WHERE book like \"%".str_replace ( "'", "&apos;", $pval["book"] )."%\"";
	$q .= "
		ORDER BY skill
	";
	$skills_db = $mysqli->query ($q);
	if ( $skills_db->num_rows == 0 )
	{
		print "no recipes found!<br><br>\n";
		// print "q: $q<br>";
		return;
	}
	$url = $_SERVER['PHP_SELF'];
	print "show recipes of skill: <span>| ";
	while ( $s = $skills_db->fetch_assoc() )
	{
		$skill = $s['skill'];
		$skills[] = $skill;
		href ( $skill, $url, "?skill=$skill" );
		print "|";
	}
	print"</span>\n";

	//
	// show recipes
	//
	$param = "";
	if ( $pval['skill'] != "" )
		$param = add_url_param ( $param, "skill=".$pval['skill'] );
	if ( $pval['book'] != "" )
		$param = add_url_param ( $param, "book=".$pval['book'] );
	if ( $pval['type'] == "P" )
	{
		$param = add_url_param ( $param, "type=%" );
		$name = "show all recipes";
	}
	else
	{
		$name = "show only preparations";
	}
	print "<h3>";
	print " | ";
	href ( $name, $url, $param );
	print " | ";

	//
	// show skills
	//
	$param = "";
	if ( $pval['type'] != "P" )
		$param = add_url_param ( $param, "type=".$pval['type'] );
	if ( $pval['book'] != "" )
		$param = add_url_param ( $param, "book=".$pval['book'] );
	if ( $pval['skill'] != "" )
	{
		href ( "show all skills", $url, $param );
		print " | ";
		$param = add_url_param ( $url, "skill=".$pval['skill'] );
	}

	//
	// show books
	//
	$param = "";
	if ( $pval['type'] != "P" )
		$param = add_url_param ( $param, "type=".$pval['type'] );
	if ( $pval['skill'] != "" )
		$param = add_url_param ( $param, "skill=".$pval['skill'] );
	if ( $pval['book'] != "" )
	{
		href ( "show all books", $url, $param );
		print " | ";
	}
	print "</h3>";

	$q = "
		SELECT
			*
		FROM
			recipes
		WHERE
	";
	if ( $pval['type'] == "P" )
	{
		$q.= "type='P'";
		// $q.= "((type='P') or (type='I'))";
	}
	else
	{
		$q.= "type LIKE '".$pval['type']."'";
	}
	if ( $pval['book'] != "" )
	{
		$q .= "AND book like \"%".str_replace ( "'", "&apos;", $pval["book"] )."%\" ";
	}
	if ( $pval['skill'] != "" )
	{
		$q .= "AND skill='".$pval['skill']."' ";
	}
	$q .= "GROUP BY name,book ORDER BY name,skill,level";
	$preps = $mysqli->query ($q);
	print $preps->num_rows." recipes found:";
	?>
	<input id="myInput" type="text" placeholder="Search Table..">
	<table id="myTable" class='recipe_table sortable hovableTable'>
		<tr>
			<th>Name</th>
			<th>Level</th>
			<th>Skill</th>
			<th>Book</th>
		</tr>
	<?php
	while ( $p = $preps->fetch_assoc() )
	{
		$param = "";
		if ( $pval['type'] != "P" )
			$param = add_url_param ( $param, "type=".$pval['type'] );
		?>
		<tr>
			<td>
				<?php
				$name = $p['name'];
				//href ( $name, $url, "?recipe=$name" );
				href ( $name." (".$p['type'].")", $url, "?recipe=$name" );
				?>
			</td>
			<td><?php print $p['level']; ?></td>
			<td><?php print $p['skill']; ?></td>
			<td>
				<?php
				$book = $p['book'];
				$param = add_url_param ( $param, "book=".$p['book'] );
				$book = str_replace ( ","," +<br>", $book );
				href ( $book, $url, $param );
				?>
			</td>
		</tr>
		<?php
	}
	?>
	</table>
	<script>
		$(document).ready(function(){
		  $("#myInput").on("keyup", function() {
			var value = $(this).val().toLowerCase();
			$("#myTable tr").filter(function() {
			  $(this).toggle($(this).text().toLowerCase().indexOf(value) > -1)
			});
		  });
		});
	</script>
	<?php
}

$mysqli = connect_db ();
print_header ("Recipes", "recipes.css" );
if ( $pval['recipe'] != "" )
	print_recipe ( $pval );
else
	show_recipes ( $pval );
print_footer ();
?>
