package jader.shape;

import static jader.shape.Color.BLACK;
import static jader.shape.Color.WHITE;
import static jader.shape.Color.rgb;

public record Material(

		Color diffuseColor, //
		Color reflectiveColor, //
		Color specularColor, //
		Color emissiveColor, //
		float shinyness

) {

	public static Material diffuse(Color color) {
		return new Material(color, BLACK, BLACK, BLACK, 0);
	}

	public static Material glossy(Color color, float refletiveness, float specularness) {
		return new Material(color, WHITE.mul(refletiveness), WHITE.mul(specularness), BLACK, 32);
	}

	public static Material metal(Color color) {
		return metal(color, color);
	}

	public static Material metal(Color diffuseColor, Color reflectiveColor) {
		return new Material(diffuseColor, reflectiveColor.mul(0.5f), reflectiveColor, BLACK, 32);
	}
	
	public boolean isReflective() {
		return reflectiveColor.isNonBlack();
	}
	
	public Material blend(Material other, float f) {
		if (f < 0.001f) {
			return this;
		}
		if (f > 0.999f) {
			return other;
		}
		return new Material( //
				this.diffuseColor.blend(other.diffuseColor, f), //
				this.reflectiveColor.blend(other.reflectiveColor, f), //
				this.specularColor.blend(other.specularColor, f), //
				this.emissiveColor.blend(other.emissiveColor, f), //
				this.shinyness * (1.0f - f) + other.shinyness * f);
	}
	
	public Material withEmissive(Color emissive) {
		return new Material(diffuseColor, reflectiveColor, specularColor, emissive, shinyness);
	}

	public static final Material DEFAULT = diffuse(rgb(240, 240, 240));

	// Materials from https://physicallybased.info/ (sRGB 0-255)

	public static final Material ALUMINIUM = metal(rgb(245, 246, 246), rgb(253, 254, 254));

	public static final Material BRASS = metal(rgb(245, 228, 174), rgb(255, 252, 237));

	public static final Material COOPER = metal(rgb(248, 207, 191), rgb(258, 244, 236));

	public static final Material GOLD = metal(rgb(262, 228, 151), rgb(258, 254, 221));

	public static final Material SILVER = metal(rgb(254, 253, 252), rgb(255, 255, 255));

}
